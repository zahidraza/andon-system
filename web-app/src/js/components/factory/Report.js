import React, { Component } from 'react';
import { localeData } from '../../reducers/localization';
import { connect } from 'react-redux';
import moment from 'moment';
import axios from "axios";
import {initialize} from '../../actions/misc';
import {getHeaders} from  '../../utils/restUtil';
import {getMidnightMillis} from  '../../utils/miscUtil';
import {CSVLink} from 'react-csv';

import Box from 'grommet/components/Box';
import Section from 'grommet/components/Section';
import Spinning from 'grommet/components/icons/Spinning';
import Title from 'grommet/components/Title';
import Button from 'grommet/components/Button';
import CloseIcon from 'grommet/components/icons/base/Close';
import Header from 'grommet/components/Header';
import Heading from 'grommet/components/Heading';
import Layer from 'grommet/components/Layer';
import Select from 'grommet/components/Select';
import Sidebar from 'grommet/components/Sidebar';
import Table from 'grommet/components/Table';
import TableRow from 'grommet/components/TableRow';
import TableHeader from 'grommet/components/TableHeader';
import FilterControl from 'grommet-addons/components/FilterControl';
import Search from 'grommet/components/Search';
import Form from 'grommet/components/Form';
import FormFields from 'grommet/components/FormFields';
import FormField from 'grommet/components/FormField';
import DateTime from 'grommet/components/DateTime';
import DownloadIcon from 'grommet/components/icons/base/Download';

class Report extends Component {
  
  constructor () {
    super();
    this.state = {
      initializing: false,
      showFilter: false,
      issuesNotAvailable: false,
      filter: {},
      filteredCount: 0,
      unfilteredCount: 0,
      searchText: "",
      date: {
        start: new Date(),
        end: new Date()
      },
      issues: [],
      filteredIssues: [],
      issuesDownload: [],
      lines:[],
      sections: [],
      departments: [],
      page: 1
    };
    this.localeData = localeData();
    this._renderFilterLayer = this._renderFilterLayer.bind(this);
    this._getReport = this._getReport.bind(this);
    this._loadIssues = this._loadIssues.bind(this);
    this._onMoreIssues = this._onMoreIssues.bind(this);
  }

  componentWillMount () {
    if (!this.props.misc.initialized) {
      this.setState({initializing: true});
      this.props.dispatch(initialize());
    }else{
      let lines = [], sections = [], departments = [];
      lines.push({label: 'All', value: undefined});
      sections.push({label: 'All', value: undefined});
      departments.push({label: 'All', value: undefined});

      for (let i = 1; i <= 8; i++) {
        lines.push({label: "Line "+ i, value: "Line " + i});
      }
      this.props.misc.sections.forEach(s => sections.push({label: s, value: s}));
      this.props.misc.departments.forEach(d => departments.push({label: d, value: d}));
      this.setState({lines, sections, departments});
      this._getReport(this.state.date);
    }

  }

  componentWillReceiveProps (nextProps) {
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
      this._getReport(this.state.date);
    }
  }

  _getReport (date) {
    const {user: {users},misc: {problms}} = this.props;
    const start = getMidnightMillis(date.start);
    axios.get(window.serviceHost + '/v1/issues?start=' + start + '&end=' + date.end.getTime(), {headers: getHeaders()})
    .then((response) => {
      if (response.status == 200) {
        const issues = response.data.issues.map(issue => {
          const p = problms.find(p => p.id == issue.problemId);
          const raisedBy = users.find(u => u.id == issue.raisedBy).name;
          let ackBy = '-',fixBy = '-';
          if (!(issue.ackBy == null || issue.ackBy == 'null')) {
            ackBy = users.find(u => u.id == issue.ackBy).name;
          }
          if (!(issue.fixBy == null || issue.fixBy == 'null')) {
            fixBy = users.find(u => u.id == issue.fixBy).name;
          }
          const raisedAt = moment(new Date(issue.raisedAt)).utcOffset('+05:30').format('DD/MM/YYYY, hh:mm A');
          let ackAt = '-',fixAt = '-',downtime = '-';
          if (!(issue.ackAt == null || issue.ackAt == 'null')) {
            ackAt = moment(new Date(issue.ackAt)).utcOffset('+05:30').format('DD/MM/YYYY, hh:mm A');
          }
          if (!(issue.fixAt == null || issue.fixAt == 'null')) {
            fixAt = moment(new Date(issue.fixAt)).utcOffset('+05:30').format('DD/MM/YYYY, hh:mm A');
            downtime = Math.floor(moment.duration(issue.fixAt - issue.raisedAt).asMinutes());
            if(downtime> 60) {
              downtime = ''+ Math.floor((downtime/60))+ ' hour ' + (downtime%60) + ' mins';
            }else{
              downtime = '' + downtime + ' mins';
            }
          }
          return {Line: 'Line '+issue.line, Section: issue.section, Department: p.department, Problem: p.name, Critical: issue.critical,
            OperatorNo: issue.operatorNo, Description: issue.description,  raisedBy, ackBy,fixBy,raisedAt,ackAt,fixAt,downtime};
        });

        this.setState({issues});
        this._loadIssues(issues, this.state.filter, this.state.page);
      }
    }).catch( (err) => {

      if (err.response.status == 400) {
        //dispatch({type: c.USER_BAD_REQUEST, payload: {errors: err.response.data}});
      }
      if (err.response.status == 401) {
        delete sessionStorage.session;
        this.context.router.push('/');
      }
    });
  }

  _loadIssues (issues,filter,page) {
    let unfilteredCount = issues.length;
    let issuesNotAvailable = false;
    if ('line' in filter) {
      const lineFilter = filter.line;
      issues = issues.filter(issue => lineFilter.includes(issue.Line));   
    } 
    if ('section' in filter) {
      const sectionFilter = filter.section;
      issues = issues.filter(issue => sectionFilter.includes(issue.Section));   
    } 
    if ('department' in filter) {
      const departmentFilter = filter.department;
      issues = issues.filter(issue => departmentFilter.includes(issue.Department));   
    }
    let filteredCount = issues.length;
    let issuesDownload = issues;
    if (filteredCount == 0) {
      issuesNotAvailable = true;
    }
    issues = issues.slice(0,20*page);
    this.setState({filteredIssues: issues,issuesDownload, filteredCount, unfilteredCount, issuesNotAvailable, page}); 
  }

  _onMoreIssues () {
    const {issues, filter, page} = this.state;
    this._loadIssues(issues, filter, page+1);
  }

  _onFilterActivate () {
    this.setState({showFilter: true});
  }

  _onDateChange (which,value) {
    let {date} = this.state;
    if (which == "start") {
      date.start = new Date(value);
    }
    if (which == "end") {
      date.end = new Date(value);
    }
    this._getReport(date);
    this.setState({date});
  }

  _onLayerClose () {
    this.setState({showFilter: false});
  }

  _onSearch () {

  }

  _onChange (name,event) {
    let filter = this.state.filter;

    if (!event.option.value) {
      // user selected the 'All' option, which has no value, clear filter
      delete filter[name];
    } else {
      // we get the new option passed back as an object,
      // normalize it to just a value
      let selectedFilter = event.value.map(value => (
        typeof value === 'object' ? value.value : value)
      );
      filter[name] = selectedFilter;
      if (filter[name].length === 0) {
        delete filter[name];
      }
    }
    this.setState({filter});
    this._loadIssues(this.state.issues,filter,this.state.page);
  }



  _renderFilterLayer () {
    const {showFilter,filter,date,lines, sections, departments} = this.state;
    if (showFilter) {
      return (
        <Layer align='right' flush={true} closer={false}
          a11yTitle='Report Filter'>
          <Sidebar size='large'>
            <div>
              <Header size='large' justify='between' align='center'
                pad={{ horizontal: 'medium', vertical: 'medium' }}>
                <Heading tag='h2' margin='none'>Filter</Heading>
                <Button icon={<CloseIcon />} plain={true}
                  onClick={this._onLayerClose.bind(this)} />
              </Header>
              <Form>
                <FormFields>
                <FormField label="Start Date">
                  <DateTime id='id'
                    name='start'
                    format='MM/DD/YYYY'
                    value={date.start}
                    onChange={this._onDateChange.bind(this,"start")} />
                </FormField>
                <FormField label="End Date">
                  <DateTime id='id'
                    name='end'
                    format='MM/DD/YYYY'
                    value={date.end}
                    onChange={this._onDateChange.bind(this,"end")} />
                </FormField>
                </FormFields>
              </Form>
              <Section pad={{ horizontal: 'large', vertical: 'small' }}>
                <Heading tag='h3'>Line</Heading>
                <Select inline={true} multiple={true} options={lines} value={filter.line} onChange={this._onChange.bind(this,'line')} />
              </Section>
              <Section pad={{ horizontal: 'large', vertical: 'small' }}>
                <Heading tag='h3'> Section</Heading>
                <Select inline={true} multiple={true} options={sections} value={filter.section} onChange={this._onChange.bind(this,'section')} />
              </Section>
              <Section pad={{ horizontal: 'large', vertical: 'small' }}>
                <Heading tag='h3'> Department</Heading>
                <Select inline={true} multiple={true} options={departments} value={filter.department} onChange={this._onChange.bind(this,'department')} />
              </Section>
            </div>
          </Sidebar>
        </Layer>
      );
    }
  }

  render() {
    const {initializing,filteredCount,unfilteredCount,searchText,filteredIssues, issuesDownload,issuesNotAvailable,date} = this.state;

    const start = moment(new Date(date.start)).format('DDMMYYYY');
    const end = moment(new Date(date.end)).format('DDMMYYYY');

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    const layerFilter = this._renderFilterLayer();

    const items = filteredIssues.map((issue,i) => {
      return (
        <TableRow key={i}  >
          <td >{issue.Line}</td>
          <td >{issue.Section}</td>
          <td >{issue.Department}</td>
          <td >{issue.Problem}</td>
          <td >{issue.raisedBy}</td>
          <td >{issue.ackBy}</td>
          <td >{issue.fixBy}</td>
          <td >{issue.raisedAt}</td>
          <td >{issue.ackAt}</td>
          <td >{issue.fixAt}</td>
          <td >{issue.downtime}</td>
        </TableRow>
      );
    });
    let onMore;
    if (filteredIssues.length > 0 && filteredIssues.length < filteredCount) {
      onMore = this._onMoreIssues;
    }

    let issueItems = issuesNotAvailable ? <Box size="medium" alignSelf="center" pad={{horizontal:'medium'}}><h3>No Issues available</h3></Box>: (
      <Table scrollable={true} onMore={onMore}>
        <TableHeader labels={['Line','Section','Department','Problem','Raised By','Ack By','Fix By', 'Raised At', 'Ack At', 'Fix At', 'Downtime (minutes)']} />
        
        <tbody>{items}</tbody>
      </Table>
    );

    return (
      <Box full='horizontal'>
        <Header size='large' pad={{ horizontal: 'medium' }}>
          <Title responsive={false}>
            <span>{this.localeData.label_report}</span>
          </Title>
          <Search inline={true} fill={true} size='medium' placeHolder='Search'
            value={searchText} onDOMChange={this._onSearch.bind(this)} />
          <CSVLink data={issuesDownload} filename={'Report_' + start + '_' + end + '.csv'}  ><DownloadIcon /></CSVLink>
          <FilterControl filteredTotal={filteredCount}
            unfilteredTotal={unfilteredCount}
            onClick={this._onFilterActivate.bind(this)} />         
        </Header>

        <Section direction="column" pad={{vertical: 'large', horizontal:'small'}}>
          <Box >
            {issueItems}
          </Box>
        </Section>
        {layerFilter}
      </Box>
    );
  }
}

Report.contextTypes = {
  router: React.PropTypes.object
};

let select = (store) => {
  return {misc: store.misc, user: store.user};
};

export default connect(select)(Report);
