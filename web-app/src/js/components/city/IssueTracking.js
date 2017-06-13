import React, { Component } from 'react';
import { localeData } from '../../reducers/localization';
import { connect } from 'react-redux';
import {initialize} from '../../actions/misc';
import {syncIssue, addIssue, updateIssue} from '../../actions/issue';
import { ISSUE_CONSTANTS as c, USER_TYPE as ut, USER_LEVEL as ul}  from '../../utils/constants';
import moment from 'moment';

import Add from "grommet/components/icons/base/Add";
import Anchor from 'grommet/components/Anchor';
import Box from 'grommet/components/Box';
import Button from 'grommet/components/Button';
import FilterControl from 'grommet-addons/components/FilterControl';
import Header from 'grommet/components/Header';
import Heading from 'grommet/components/Heading';
import Layer from 'grommet/components/Layer';
import Search from 'grommet/components/Search';
import Section from 'grommet/components/Section';
import Sidebar from 'grommet/components/Sidebar';
import Spinning from 'grommet/components/icons/Spinning';
import Table from 'grommet/components/Table';
import TableRow from 'grommet/components/TableRow';
import TableHeader from 'grommet/components/TableHeader';
//import Trash from "grommet/components/icons/base/Trash";
import Title from 'grommet/components/Title';
import Form from 'grommet/components/Form';
import Footer from 'grommet/components/Footer';
import FormFields from 'grommet/components/FormFields';
import FormField from 'grommet/components/FormField';
import Select from 'grommet/components/Select';
import CloseIcon from 'grommet/components/icons/base/Close';
import ViewIcon from 'grommet/components/icons/base/View';
import List from 'grommet/components/List';
import ListItem from 'grommet/components/ListItem';
import SyncIcon from 'grommet/components/icons/base/Sync';


class IssueTracking extends Component {
  
  constructor () {
    super();
    this.state = {
      initializing: false,
      searchText: '',
      issues: [],
      issue: {},
      teams: [],
      team: '',
      problems: [],
      buyers: [],
      buyer: 'Select Buyer',
      filter: {},
      filterActive: false,
      unfilteredCount: 0,
      filteredCount: 0,
      teamFilterItems: []

    };
    this.localeData = localeData();
    this._loadIssue = this._loadIssue.bind(this);
    this._sortIssue = this._sortIssue.bind(this);
    this._renderLayerAdd = this._renderLayerAdd.bind(this);
    this._renderLayerView = this._renderLayerView.bind(this);
    this._renderLayerFilter = this._renderLayerFilter.bind(this);
    this._loadFilter = this._loadFilter.bind(this);
  }

  componentWillMount () {
    this.props.dispatch(syncIssue(this.props.issue.issueSync));
    if (!this.props.misc.initialized) {
      this.setState({initializing: true});
      this.props.dispatch(initialize());
    } else {
      const {teams, problems} = this.props.misc;
      this._loadFilter(teams);
      this.setState({teams: teams, problems: problems});
    }

  }

  componentWillReceiveProps (nextProps) {
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
      const {misc: {teams, problems}, issue: {issues, filter}} = nextProps;
      this._loadIssue(issues, filter);
      this._loadFilter(teams);
      this.setState({teams: teams, problems: problems});
    }
    if (this.props.issue.toggleStatus != nextProps.issue.toggleStatus) {
      const {issues,filter} = nextProps.issue;
      this._loadIssue(issues, filter);
    }
  }

  _loadFilter (teams) {
    let teamFilterItems = [ {label: 'All', value: undefined}];
    teams.forEach(team => teamFilterItems.push({label: team, value: team}));
    this.setState({teamFilterItems}); 
  }

  _loadIssue (issues, filter) {
    const {misc: {buyers}, user: {users} } = this.props;
    
    //Filter non deleted issues
    issues = issues.filter(issue => !issue.deleted );
    let unfilteredCount = issues.length;

    if ( 'team' in filter) {
      const teamFilter = filter.team;
      issues = issues.filter(issue => teamFilter.includes(issue.buyer.team));
    }
    issues.forEach(issue => {
      issue.buyer = buyers.find(b => b.id == issue.buyerId);
      issue.userRaised = users.find(u => u.id == issue.raisedBy);
      issue.userAck = users.find(u => u.id == issue.ackBy);
      issue.userFixed = users.find(u => u.id == issue.fixedBy);
    });

    let filteredCount = issues.length;
    issues = this._sortIssue(issues);
    this.setState({issues, filteredCount, unfilteredCount});
  }

  _sortIssue (issues) {
    issues = issues.sort((a,b) => {
      let x1 = ( (a.fixAt != null) ? 2 : (a.ackAt != null ? 1 : 0) ) ;
      let x2 = ( (b.fixAt != null) ? 2 : (b.ackAt != null ? 1 : 0) ) ;
      let d = x1 - x2;
      if (d == 0) {  //under same state [raised,acknowledged,fixed], 
        return b.raisedAt - a.raisedAt;
      }else {
        return d;
      }
    });
    return issues;
  }

  _onSync () {
    this.props.dispatch(syncIssue());
  }

  _raiseIssue () {
    const {buyer, issue} = this.state;

    issue.buyerId = this.props.misc.buyers.find(b => b.name == buyer).id;
    issue.raisedBy = sessionStorage.userId;

    this.props.dispatch(addIssue(issue));
  }

  _updateIssue(operation, event) {
    console.log("operation = " + operation);
    const {issue} = this.state;
    console.log(issue);
    if (operation == 'ack') {
      this.props.dispatch(updateIssue({id: issue.id, ackBy: sessionStorage.userId}, 'OP_ACK'));
    } else if (operation == 'fix') {
      this.props.dispatch(updateIssue({id: issue.id, fixBy: sessionStorage.userId}, 'OP_FIX'));
    } else if (operation == 'delete') {
      this.props.dispatch(updateIssue({id: issue.id}, 'OP_DEL'));
    }
  }

  _onChangeInput (event) {
    let issue = this.state.issue;
    issue[event.target.getAttribute('name')] = event.target.value;
    this.setState({issue});
  }

  _onChangeFilter (name,event) {
    let {filter} = this.state;

    if (!event.option.value) {
      delete filter[name];
    } else {
      let x = event.value.map(value => (
        typeof value === 'object' ? value.value : value)
      );
      filter[name] = x;
      if (filter[name].length === 0) {
        delete filter[name];
      }
    }
    this.setState({filter});
    this._loadIssue(this.props.issue.issues, filter);
  }

  _onFilter (filter, event) {
    let value = event.value;
    
    if (filter == 'team') {
      let buyers = this.props.misc.buyers.filter(buyer => buyer.team == value).map(buyer => buyer.name);
      this.setState({buyers, buyer: buyers[0], team: value});
    } else if (filter == 'buyer') {
      this.setState({buyer: value});
    } else if (filter == 'problem') {
      let issue = this.state.issue;
      issue.problem = value;
      this.setState({issue});
    }
  }

  _onAddClick () {
    let {teams, problems, issue} = this.state;
    issue.problem = problems[0];
    let team = teams[0];
    let buyers = this.props.misc.buyers.filter(buyer => buyer.team == team).map(buyer => buyer.name);
    let buyer = buyers[0];
    this.setState({adding: true, issue, team, buyers, buyer});
    this.props.dispatch({type: c.ISSUE_ADD_FORM_TOGGLE, payload: {adding: true}});
  }

  _onViewClick (index, event) {
    this.setState({issue: this.state.issues[index]});
    this.props.dispatch({type: c.ISSUE_VIEW_TOGGLE, payload: {viewing: true}});
  }

  _onSearch (event) {
    let value = event.target.value;
    let {issue: {issues}, misc: {buyers}, user: {users}} = this.props;

    issues.forEach(issue => {
      issue.buyer = buyers.find(b => b.id == issue.buyerId);
      issue.userRaised = users.find(u => u.id == issue.raisedBy);
    });

    issues = issues.filter(issue => issue.userRaised.name.toLowerCase().includes(value.toLowerCase()) || issue.buyer.name.toLowerCase().includes(value.toLowerCase()));
    this.setState({searchText: value});
    if (value.length == 0) {
      this._loadIssue(issues, this.state.filter);
    }else {
      this._loadIssue(issues, {});
    }
  }

  _onFilterActivate () {
    this.setState({filterActive: true});
  }

  _onCloseLayer (layer, event) {
    if (layer == 'add') {
      this.props.dispatch({type: c.ISSUE_ADD_FORM_TOGGLE, payload: {adding: false}});
    } else if (layer == 'view') {
      this.props.dispatch({type: c.ISSUE_VIEW_TOGGLE, payload: {viewing: false}});
    } else if (layer == 'filter') {
      this.setState({filterActive: false});
    }
  }

  _renderLayerFilter () {
    const {filter,filterActive} = this.state;

    if (!filterActive) return null;

    return (
      <Layer align='right' flush={true} closer={false} a11yTitle='Team Filter'>
        <Sidebar size='large'>
          <div>
            <Header size='large' justify='between' align='center'
              pad={{ horizontal: 'medium', vertical: 'medium' }}>
              <Heading tag='h2' margin='none'>Filter</Heading>
              <Button icon={<CloseIcon />} plain={true}
                onClick={this._onCloseLayer.bind(this, 'filter')} />
            </Header>

            <Section pad={{ horizontal: 'large', vertical: 'small' }}>
              <Heading tag='h3'>Team</Heading>
              <Select inline={true} multiple={true} options={this.state.teamFilterItems} value={filter.team} onChange={this._onChangeFilter.bind(this,'team')} />
            </Section>
          </div>
        </Sidebar>
      </Layer>
    );
  }

  _renderLayerView () {
    const {issue} = this.state;
    const {viewing, busy} = this.props.issue;

    if (!viewing) return null;

    const busyIcon = busy ? <Spinning /> : null;
    const user = this.props.user.users.find(u => u.id == parseInt(sessionStorage.userId));
    let actionControl;
    if (sessionStorage.userType == ut.SAMPLING && user.level == ul.LEVEL0) {
      if (issue.raisedBy == sessionStorage.userId) {
        actionControl = (
          <Box size='small' alignSelf='center' >
            <Button icon={busyIcon} label='Delete' onClick={this._updateIssue.bind(this, 'delete')} primary={true}/>
          </Box>
        );
      }
    }
    if (sessionStorage.userType == ut.MERCHANDISING) {
      if (issue.ackAt == null) {
        if (user.buyers.some(b => b.id == issue.buyerId)) {
          if (issue.processingAt > 1) {
            if (user.level == ul.LEVEL2) {
              actionControl = (
                <Box size='small' alignSelf='center' >
                  <Button icon={busyIcon} label='Acknowledge' onClick={this._updateIssue.bind(this, 'ack')} primary={true}/>
                </Box>
              );
            }
          } else {
            if (user.level == ul.LEVEL1) {
              actionControl = (
                <Box size='small' alignSelf='center' >
                  <Button icon={busyIcon} label='Acknowledge' onClick={this._updateIssue.bind(this, 'ack')} primary={true}/>
                </Box>
              );
            }
          }
        }
      } else if (issue.fixAt == null) {
        if (user.buyers.some(b => b.id == issue.buyerId)) {
          if (issue.processingAt > 1) {
            if (user.level == ul.LEVEL2) {
              actionControl = (
                <Box size='small' alignSelf='center' >
                  <Button icon={busyIcon} label='Fix' onClick={this._updateIssue.bind(this, 'fix')} primary={true}/>
                </Box>
              );
            }
          } else {
            if (user.level == ul.LEVEL1) {
              actionControl = (
                <Box size='small' alignSelf='center' >
                  <Button icon={busyIcon} label='Fix' onClick={this._updateIssue.bind(this, 'fix')} primary={true}/>
                </Box>
              );
            }
          }
        }
      } 
    }

    return (
      <Layer onClose={this._onCloseLayer.bind(this, 'view')}  closer={true} align="center">
        <Box size="large"  pad={{vertical: 'none', horizontal:'small'}}>
          <Header><Heading tag="h3" strong={true} >Mapping Details</Heading></Header>
          <List>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Problem: </span>
              <span className="secondary">{issue.problem}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Team: </span>
              <span className="secondary">{issue.buyer.team}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Buyer: </span>
              <span className="secondary">{issue.buyer.name}</span>
            </ListItem><ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Raised At: </span>
              <span className="secondary">{moment(new Date(issue.raisedAt)).utcOffset('+05:30').format('DD MMM, YY hh:mm A')}</span>
            </ListItem><ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Raised By: </span>
              <span className="secondary">{issue.userRaised.name}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Acknowledged At: </span>
              <span className="secondary">{issue.ackAt == null ? '-' : moment(new Date(issue.ackAt)).utcOffset('+05:30').format('DD MMM, YY hh:mm A')}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Acknowledged By: </span>
              <span className="secondary">{issue.userAck == undefined ? '-' : issue.userAck.name}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Fixed At: </span>
              <span className="secondary">{issue.fixAt == null ? '-' : moment(new Date(issue.fixAt)).utcOffset('+05:30').format('DD MMM, YY hh:mm A')}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Fixed By: </span>
              <span className="secondary">{issue.userFixed == undefined ? '-' : issue.userFixed.name}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Status: </span>
              <span className="secondary">{issue.processingAt == 4 ? 'Fixed': 'Processing at level ' + issue.processingAt}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Remarks: </span>
              <span className="secondary"/>
            </ListItem>
            <ListItem justify="end" pad={{vertical:'small',horizontal:'small'}} >
              {issue.description}
            </ListItem>
          </List>
          <Box pad={{vertical: 'medium', horizontal:'small'}} />
          {actionControl}

        </Box>
        <Box pad={{vertical: 'medium', horizontal:'small'}} />
      </Layer>
    );
  }

  _renderLayerAdd () {
    const {team, teams, buyer, buyers, problems, issue} = this.state;
    if (! this.props.issue.adding) return null;

    const busyIcon = this.props.issue.busy ? <Spinning /> : null;

    return (
      <Layer onClose={this._onCloseLayer.bind(this, 'add')}  closer={true} align="center">
        <Form>
          <Header><Heading tag="h3" strong={true}>Raise Issue</Heading></Header>
          <FormFields>
            <FormField label='Team'>
              <Select options={teams} value={team} onChange={this._onFilter.bind(this, 'team')}/>
            </FormField >
            <FormField label='Buyer' >
              <Select options={buyers} value={buyer} onChange={this._onFilter.bind(this, 'buyer')}/>
            </FormField>
            <FormField label='Problem' >
              <Select options={problems} value={issue.problem} onChange={this._onFilter.bind(this, 'problem')}/>
            </FormField>
            <FormField label="Remarks" error=''>
              <input type="text" name="description" value={issue.description == undefined ? '' : issue.description} onChange={this._onChangeInput.bind(this)} />
            </FormField>
          </FormFields>
          <Footer pad={{"vertical": "medium"}} >
            <Button icon={busyIcon} label="Raise" primary={true}  onClick={this._raiseIssue.bind(this)} />
          </Footer>
        </Form>
      </Layer>
    );

  }

  render() {
    const {initializing, issues, searchText, filteredCount, unfilteredCount} = this.state;

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    const busy = this.props.issue.busy ? <Spinning /> : null;

    //red color = #f4708b ,  blue color = #3e95f2, green = #58ba43

    const items = issues.map((issue, index)=>{
      let style;
      if (issue.fixAt != null) {
        style = {backgroundColor: '#58ba43'};
      }else if (issue.ackAt != null) {
        style = {backgroundColor: '#3e95f2'};
      }else {
        style = {backgroundColor: '#f4708b'};
      }
      return (
        <TableRow key={index} style={style} >
          <td >{issue.problem}</td>
          <td >{issue.buyer.team}</td>
          <td >{issue.buyer.name}</td>
          <td >{issue.userRaised.name}</td>
          <td >{moment(new Date(issue.raisedAt)).utcOffset('+05:30').format('DD MMM, YY hh:mm A')}</td>
          <td><Button icon={<ViewIcon />} onClick={this._onViewClick.bind(this,index)} /></td>
        </TableRow>
      );
    });

    const layerAdd = this._renderLayerAdd();
    const layerView = this._renderLayerView();
    const layerFilter = this._renderLayerFilter();

    const user = this.props.user.users.find(u => u.id == parseInt(sessionStorage.userId));
    let addControl;
    if (sessionStorage.userType == ut.SAMPLING && user.level == ul.LEVEL0) {
      addControl = (<Anchor icon={<Add />} a11yTitle={`Add Issue`} onClick={this._onAddClick.bind(this)}/>);
    }

    return (
      <Box full='horizontal'>
        <Header size='large' pad={{ horizontal: 'medium' }}>
          <Title responsive={false}>
            <span>{this.localeData.label_issue_tracking}</span>
          </Title>
          <Search inline={true} fill={true} size='medium' placeHolder='Search buyer, raisedBy'
            value={searchText} onDOMChange={this._onSearch.bind(this)} />
          {addControl}
          <Anchor icon={<SyncIcon />} a11yTitle={`Sync Issue`} onClick={this._onSync.bind(this)}/>
          <FilterControl filteredTotal={filteredCount}
            unfilteredTotal={unfilteredCount}
            onClick={this._onFilterActivate.bind(this)} />
        </Header>

        <Section direction="column" pad={{vertical: 'large', horizontal:'small'}}>
          <Box>{busy}</Box>
          <Box >
            <Table>
              <TableHeader labels={['Problem Name', 'Team', 'Buyer', 'RaisedBy' , 'Raised At', 'View']} />
              
              <tbody>{items}</tbody>
            </Table>
          </Box>
        </Section>
        {layerAdd}
        {layerView}
        {layerFilter}
      </Box>
    );
  }
}

IssueTracking.contextTypes = {
  router: React.PropTypes.object
};

let select = (store) => {
  return {misc: store.misc, issue: store.issue, user: store.user};
};

export default connect(select)(IssueTracking);
