import React, { Component } from 'react';
import { localeData } from '../../reducers/localization';
import { connect } from 'react-redux';
import moment from 'moment';
import axios from "axios";
import {initialize} from '../../actions/misc';
import {getHeaders} from  '../../utils/restUtil';
import {CSVLink} from 'react-csv';

import AppHeader from '../AppHeader';
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
import Sort from 'grommet-addons/components/Sort';
import Table from 'grommet/components/Table';
import TableRow from 'grommet/components/TableRow';
import TableHeader from 'grommet/components/TableHeader';
import FilterControl from 'grommet-addons/components/FilterControl';
//import HelpIcon from 'grommet/components/icons/base/Help';
import Search from 'grommet/components/Search';
import Form from 'grommet/components/Form';
import FormFields from 'grommet/components/FormFields';
import FormField from 'grommet/components/FormField';
import DateTime from 'grommet/components/DateTime';

class Report extends Component {
  
  constructor () {
    super();
    this.state = {
      initializing: false,
      showFilter: false,
      filter: {},
      sort: "buyer:asc",
      filteredCount: 0,
      unfilteredCount: 0,
      searchText: "",
      date: {
        start: new Date(),
        end: new Date()
      },
      issues: []
    };
    this.localeData = localeData();
    this._renderFilterLayer = this._renderFilterLayer.bind(this);
    this._getReport = this._getReport.bind(this);
  }

  componentWillMount () {
    console.log('componentWillMount');
    if (!this.props.misc.initialized) {
      this.setState({initializing: true});
      this.props.dispatch(initialize());
    }else{
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
    const {user: {users},misc: {buyers}} = this.props;

    axios.get(window.serviceHost + '/v2/issues?start=' + date.start.getTime() + '&end=' + (date.end.getTime() + (1000*60*60*24)), {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        const issues = response.data.issues.map(issue => {
          const buyer = buyers.find(b => b.id == issue.buyerId);
          const raisedBy = users.find(u => u.id == issue.raisedBy).name;
          let ackBy = '-',fixBy = '-';
          if (!(issue.ackBy == null || issue.ackBy == 'null')) {
            ackBy = users.find(u => u.id == issue.ackBy).name;
          }
          if (!(issue.fixBy == null || issue.fixBy == 'null')) {
            fixBy = users.find(u => u.id == issue.fixBy).name;
          }
          const raisedAt = moment(new Date(issue.raisedAt)).format('DD/MM/YYYY hh:mm a');
          let ackAt = '-',fixAt = '-',downtime = '-';
          if (!(issue.ackAt == null || issue.ackAt == 'null')) {
            ackAt = moment(new Date(issue.ackAt)).format('DD/MM/YYYY hh:mm a');
          }
          if (!(issue.fixAt == null || issue.fixAt == 'null')) {
            fixAt = moment(new Date(issue.fixAt)).format('DD/MM/YYYY hh:mm a');
            downtime = Math.trunc((issue.fixAt - issue.raisedAt)/(1000*60));
          }
          return {Team: buyer.team, Buyer: buyer.name,Problem: issue.problem, Description: issue.description,  raisedBy, ackBy,fixBy,raisedAt,ackAt,fixAt,downtime};
        });
        console.log(issues);

        this.setState({issues});
      }
    }).catch( (err) => {
      console.log(err);
      if (err.response.status == 400) {
        //dispatch({type: c.USER_BAD_REQUEST, payload: {errors: err.response.data}});
      }
      if (err.response.status == 401) {
        delete sessionStorage.session;
        this.context.router.push('/');
      }
    });
  }

  _onFilterActivate () {
    console.log('_onFilterActivate');
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
      console.log(selectedFilter);
      filter[name] = selectedFilter;
      if (filter[name].length === 0) {
        delete filter[name];
      }
    }
    this.setState({filter});
  }

  _onChangeSort (sort) {
    let sortString = `${sort.value}:${sort.direction}`;
    this.setState({sort: sortString});
  }

  _renderFilterLayer () {
    const {showFilter,filter,sort,date} = this.state;
    console.log(this.state);
    const [sortProperty, sortDirection] = sort.split(':');

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
                <Heading tag='h3'>Team</Heading>
                <Select inline={true} multiple={true} options={this.props.misc.teams} value={filter.team} onChange={this._onChange.bind(this,'team')} />
              </Section>
              <Section pad={{ horizontal: 'large', vertical: 'small' }}>
                <Heading tag='h2'>Sort</Heading>
                <Sort options={[
                  { label: 'Buyer', value: 'buyer', direction: 'asc' }
                ]} value={sortProperty} direction={sortDirection}
                onChange={this._onChangeSort.bind(this)} />
              </Section>
            </div>
          </Sidebar>
        </Layer>
      );
    }
  }

  render() {
    const {initializing,filteredCount,unfilteredCount,searchText,issues} = this.state;

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

    const items = issues.map((issue,i) => {
      return (
        <TableRow key={i}  >
          <td >{issue.Team}</td>
          <td >{issue.Buyer}</td>
          <td >{issue.Problem}</td>
          <td >{issue.Description}</td>
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

    return (
      <Box full='horizontal'>
        <AppHeader/>

        <Header size='large' pad={{ horizontal: 'medium' }}>
          <Title responsive={false}>
            <span>{this.localeData.label_report}</span>
          </Title>
          <Search inline={true} fill={true} size='medium' placeHolder='Search'
            value={searchText} onDOMChange={this._onSearch.bind(this)} />
          <FilterControl filteredTotal={filteredCount}
            unfilteredTotal={unfilteredCount}
            onClick={this._onFilterActivate.bind(this)} />
        </Header>

        <Section direction="column" pad={{vertical: 'large', horizontal:'small'}}>
          <Box >
            <CSVLink data={issues} filename="report.csv" >download</CSVLink>
            <Table>
              <TableHeader labels={['Team','Buyer','Problem','Description','Raised By','Ack By','Fix By', 'Raised At', 'Ack At', 'Fix At', 'Downtime (minutes)']} />
              
              <tbody>{items}</tbody>
            </Table>
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
