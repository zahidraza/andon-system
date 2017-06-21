import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';
import { localeData } from '../../reducers/localization';
import {initialize} from '../../actions/misc';
import {removeUser}  from '../../actions/user';
import {USER_CONSTANTS as c,USER_ROLE as ur, USER_TYPE as ut}  from '../../utils/constants';

import Add from "grommet/components/icons/base/Add";
import Anchor from 'grommet/components/Anchor';
import Box from 'grommet/components/Box';
import Button from 'grommet/components/Button';
import Edit from "grommet/components/icons/base/Edit";
import FilterControl from 'grommet-addons/components/FilterControl';
import Header from 'grommet/components/Header';
//import HelpIcon from 'grommet/components/icons/base/Help';
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
      const {user: {users,filter,sort}, misc: {desgns}} = this.props;
      this._loadUser(this._filterUserByUserType(users),desgns,filter,sort);
    }
  }

  componentWillReceiveProps (nextProps) {
    console.log('componentWillReceiveProps');
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
    }
    if (this.props.user.toggleStatus != nextProps.user.toggleStatus) {
      const {user: {users,filter,sort}, misc: {desgns}} = nextProps;
      this._loadUser(this._filterUserByUserType(users), desgns, filter,sort);
    }
  }

  _filterUserByUserType (users) {
    const userType = sessionStorage.userType;
    return users.filter(u => u.userType == userType);
  }

  _loadUser (users, desgns,filter,sort) {
    users = users.map(u => {
      if (u.desgnId != null) {
        u.designation = desgns.find(desgn => desgn.id == u.desgnId).name;
      }else {
        u.designation = '-';
      }
      return u;
    });

    let unfilteredCount = users.length;
    if ('level' in filter) {
      const levelFilter = filter.level;
      users = users.filter(u => levelFilter.includes(u.level));      
    }
    if ('desgn' in filter) {
      const desgnFilter = filter.desgn;
      users = users.filter(u => desgnFilter.includes(u.designation));      
    }
    let filteredCount = users.length;
    users = this._userSort(users,sort);
    this.setState({users, filteredCount, unfilteredCount});    
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
    const {user: {users,filter,sort}, misc: {desgns}}= this.props;
    let value = event.target.value;
    let usrs = this._filterUserByUserType(users);
    usrs = usrs.filter(u => u.name.toLowerCase().includes(value.toLowerCase()) || u.email.toLowerCase().includes(value.toLowerCase()));

    this.setState({searchText: value});
    if (value.length == 0) {
      this._loadUser(usrs, desgns, filter,sort);
    }else{
      this._loadUser(usrs, desgns,{},sort);
    }
  }

  _onFilterActivate () {
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
    this.props.dispatch({type: c.USER_EDIT_FORM_TOGGLE, payload:{editing: true,user: {...users[index]}}});
    this.context.router.push('/user/edit');
  }

//  _onHelpClick () {
//    window.open("http://localhost:8080/help/user");
//  }

  render() {
    const {users, searchText, filterActive,filteredCount,unfilteredCount,initializing } = this.state;

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    let tableHeaders = ['Name','Username','Level','Mobile'];
    if (sessionStorage.userType == ut.FACTORY) {
      tableHeaders.push('Designation');
    } else {
      tableHeaders.push('Role');
    }
    if (sessionStorage.role == ur.ROLE_ADMIN) {
      tableHeaders.push('ACTION');
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
          <td >{u.level}</td>
          <td >{u.mobile}</td>
          <td >{u.userType == ut.FACTORY ? u.designation : u.role}</td>
          {modControl}
        </TableRow>
      );
    });

    const layerFilter = filterActive ? <UserFilter onClose={this._onFilterDeactivate.bind(this)}/> : null;

    let addControl;
    if (sessionStorage.role == ur.ROLE_ADMIN) {
      addControl = (<Anchor icon={<Add />} path='/user/add' a11yTitle={`Add User`} onClick={this._onAddClick.bind(this)}/>);
    }

    return (
      <Box full='horizontal'>
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
