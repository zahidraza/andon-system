import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';
import { localeData } from '../../reducers/localization';
import {initialize} from '../../actions/misc';
import {removeUser}  from '../../actions/user';
import {USER_CONSTANTS as c,USER_ROLE as ur}  from '../../utils/constants';

import AppHeader from '../AppHeader';
import Add from "grommet/components/icons/base/Add";
import Anchor from 'grommet/components/Anchor';
import Box from 'grommet/components/Box';
import Button from 'grommet/components/Button';
import Edit from "grommet/components/icons/base/Edit";
import FilterControl from 'grommet-addons/components/FilterControl';
import Header from 'grommet/components/Header';
import HelpIcon from 'grommet/components/icons/base/Help';
import Search from 'grommet/components/Search';
import Section from 'grommet/components/Section';
import Spinning from 'grommet/components/icons/Spinning';
import UserFilter from './UserFilter';
import Table from 'grommet/components/Table';
import TableRow from 'grommet/components/TableRow';
import TableHeader from 'grommet/components/TableHeader';
import Trash from "grommet/components/icons/base/Trash";
import Title from 'grommet/components/Title';

class User extends Component {
  
  constructor () {
    super();
    this.state = {
      initializing: false,
      errors: [],
      users: [],
      user: {},
      searchText: '',
      filterActive: false,
      filteredCount: 0,
      unfilteredCount: 0,
      tableHeaders: []
    };
    this.localeData = localeData();
    this._loadUser = this._loadUser.bind(this);
    this._userSort = this._userSort.bind(this);
  }

  componentWillMount () {
    console.log('componentWillMount');
    if (!this.props.misc.initialized) {
      this.setState({initializing: true});
      this.props.dispatch(initialize());
    }else {
      const {users,filter,sort} = this.props.user;
      this._loadUser(this._filterUserByUserType(users),filter,sort);
    }
    let tableHeaders = ['Name','Email','Role','Level','Mobile'];
    if (sessionStorage.role == ur.ROLE_ADMIN) {
      tableHeaders.push('ACTION');
    }
    this.setState({tableHeaders});
  }

  componentWillReceiveProps (nextProps) {
    console.log('componentWillReceiveProps');
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
    }
    if (this.props.user.toggleStatus != nextProps.user.toggleStatus) {
      const {users,filter,sort} = nextProps.user;
      this._loadUser(this._filterUserByUserType(users),filter,sort);
    }
  }

  _filterUserByUserType (users) {
    const userType = sessionStorage.userType;
    return users.filter(u => u.userType == userType);
  }

  _loadUser (users,filter,sort) {
    console.log("_loadUser()");
    if ('level' in filter) {
      const levelFilter = filter.level;
      let list1 = users.filter(u => levelFilter.includes(u.level));
      list1 = this._userSort(list1,sort);
      this.setState({users: list1, filteredCount: list1.length, unfilteredCount: users.length});    
    } else {
      users = this._userSort(users,sort);
      this.setState({users: users, filteredCount: users.length, unfilteredCount: users.length}); 
    }
  }

  _userSort (users,sort) {
    const [sortProperty,sortDirection] = sort.split(':');
    let result = users.sort((a,b) => {
      if (sortProperty == 'name' && sortDirection == 'asc') {
        return (a.name < b.name) ? -1 : 1;
      } else if (sortProperty == 'name' && sortDirection == 'desc') {
        return (a.name > b.name) ? -1 : 1;
      }
    });
    return result;
  }

  _onSearch (event) {
    console.log('_onSearch');
    let value = event.target.value;

    let users = this.props.user.users.filter(u => u.name.includes(value) || u.email.includes(value));

    this.setState({searchText: value, users});

  }

  _onFilterActivate () {
    console.log(this.props.user.filter);
    console.log(this.props.user.sort);
    this.setState({filterActive: true});
  }

  _onFilterDeactivate () {
    this.setState({filterActive: false});
  }

  _onChangeInput ( event ) {
    var user = this.state.user;
    user[event.target.getAttribute('name')] = event.target.value;
    this.setState({user: user});
  }

  _onAddClick () {
    console.log('_onAddClick');
    this.props.dispatch({type: c.USER_ADD_FORM_TOGGLE,payload: {adding: true}});
  }

  _onRemoveClick (index) {
    console.log('_onRemoveClick');
    let value = confirm('Are you sure to delete this User?');
    if (!value) {
      return;
    }
    const {users} = this.state;
    this.props.dispatch(removeUser(users[index]));
  }

  _onEditClick (index) {
    console.log('_onEditClick');
    const {users} = this.state;
    this.props.dispatch({type: c.USER_EDIT_FORM_TOGGLE, payload:{editing: true,user: users[index]}});
    this.context.router.push('/user2/edit');
  }

  _onHelpClick () {
    window.open("http://localhost:8080/help/user");
  }

  render() {
    const {users, searchText, filterActive,filteredCount,unfilteredCount,tableHeaders, initializing } = this.state;

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    const items = users.map((u, index)=>{
      let modControl;
      if (sessionStorage.role == ur.ROLE_ADMIN) {
        modControl =( 
          <td style={{textAlign: 'right', padding: 0}}>
              <Button icon={<Edit />} onClick={this._onEditClick.bind(this,index)} />
              <Button icon={<Trash />} onClick={this._onRemoveClick.bind(this,index)} />
          </td>
        );
      }
      return (
        <TableRow key={index}  >
          <td >{u.name}</td>
          <td >{u.email}</td>
          <td >{u.role}</td>
          <td >{u.level}</td>
          <td >{u.mobile}</td>
          {modControl}
        </TableRow>
      );
    });

    const layerFilter = filterActive ? <UserFilter onClose={this._onFilterDeactivate.bind(this)}/> : null;

    let addControl;
    if (sessionStorage.role == ur.ROLE_ADMIN) {
      addControl = (<Anchor icon={<Add />} path='/user2/add' a11yTitle={`Add User`} onClick={this._onAddClick.bind(this)}/>);
    }

    return (
      <Box full='horizontal'>
        <AppHeader/>

        <Header size='large' pad={{ horizontal: 'medium' }}>
          <Title responsive={false}>
            <span>{this.localeData.label_user}</span>
          </Title>
          <Search inline={true} fill={true} size='medium' placeHolder='Search'
            value={searchText} onDOMChange={this._onSearch.bind(this)} />
          {addControl}
          <FilterControl filteredTotal={filteredCount}
            unfilteredTotal={unfilteredCount}
            onClick={this._onFilterActivate.bind(this)} />
          <Button icon={<HelpIcon />} onClick={this._onHelpClick.bind(this)}/>
        </Header>

        <Section direction="column" pad={{vertical: 'large', horizontal:'small'}}>
          <Box >
            <Table>
              <TableHeader labels={tableHeaders} />
              
              <tbody>{items}</tbody>
            </Table>
          </Box>
        </Section>
        {layerFilter}
      </Box>
    );
  }
}

User.contextTypes = {
  router: PropTypes.object.isRequired
};

let select = (store) => {
  return { user: store.user, misc: store.misc};
};

export default connect(select)(User);
