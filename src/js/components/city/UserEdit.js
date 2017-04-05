import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';
import { localeData } from '../../reducers/localization';
import {updateUser}  from '../../actions/user';
import {USER_CONSTANTS as c, USER_ROLE as ur, USER_LEVEL as ul}  from '../../utils/constants';
import {initialize} from '../../actions/misc';

import AppHeader from '../AppHeader';
import Article from 'grommet/components/Article';
import Header from 'grommet/components/Header';
import Heading from 'grommet/components/Heading';
import Form from 'grommet/components/Form';
import Footer from 'grommet/components/Footer';
import FormFields from 'grommet/components/FormFields';
import FormField from 'grommet/components/FormField';
import Box from 'grommet/components/Box';
import Section from 'grommet/components/Section';
import Select from 'grommet/components/Select';
import Spinning from 'grommet/components/icons/Spinning';
import Button from 'grommet/components/Button';
import CloseIcon from 'grommet/components/icons/base/Close';
import Anchor from 'grommet/components/Anchor';


class UserEdit extends Component {

  constructor () {
    super();

    this.state = {
      initializing: false,
      user: {},
      errors: []
    };

    this.localeData = localeData();
  }

  componentWillMount () {
    console.log('componentWillMount');
    if (!this.props.misc.initialized) {
      this.setState({initializing: true});
      this.props.dispatch(initialize());
    }else{
      this.setState({user: this.props.user.user});
    }
  }

  componentWillReceiveProps (nextProps) {
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
      this.setState({user: nextProps.user.user});
    }
    if (!nextProps.user.editing) {
      this.context.router.push('/user2');
    }
  }

  _onSubmit (event) {
    event.preventDefault();
    let {user} = this.state;
    console.log(user);
    this.props.dispatch(updateUser(user));
  }

  _onChange (event) {
    let user = this.state.user;
    user[event.target.getAttribute('name')] = event.target.value;
    this.setState({user: user});
  }

  _onChangeAddress (event) {
    let user = this.state.user;
    user.address[event.target.getAttribute('name')] = event.target.value;
    this.setState({user: user});
  }

  _onFilter (event) {
    let {user} = this.state;
    user[event.target.getAttribute('name')] = event.value;
    if (event.value == ur.ROLE_ADMIN) {
      user.level = ul.LEVEL4;
    }
    this.setState({user});
  }


  _onClose (event) {
    this.props.dispatch({type: c.USER_EDIT_FORM_TOGGLE, payload: {adding: false}});
  }


  render () {
    const {user,errors,initializing} = this.state;

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    const  levelFilter = user.role == ur.ROLE_ADMIN ? null : (
      <FormField label="User Level" htmlFor="level" error={errors[0]}>
        <Select id="level" name="level" options={[ul.LEVEL1, ul.LEVEL2, ul.LEVEL3]}
          value={user.level}  onChange={this._onFilter.bind(this)} />
      </FormField>
    );

    return (
      <Box>
        <AppHeader/>
        <Section>
          <Article align="center" pad={{horizontal: 'medium'}} primary={true}>
            <Form onSubmit={this._onSubmit}>

              <Header size="large" justify="between" pad="none">
                <Heading tag="h2" margin="none" strong={true}>{this.localeData.label_user_edit}</Heading>
                <Anchor icon={<CloseIcon />} path="/user2" a11yTitle='Close Add User Form' onClick={this._onClose.bind(this)} />
              </Header>

              <FormFields>

                <fieldset>
                  <FormField label="User Role" htmlFor="sType" error={errors[0]}>
                    <Select id="role" name="role" options={[ur.ROLE_ADMIN, ur.ROLE_USER]}
                      value={user.role}  onChange={this._onFilter.bind(this)} />
                  </FormField>
                  {levelFilter}
                  <FormField label="User Name" error={errors[0]}>
                    <input type="text" name="name" value={user.name} onChange={this._onChange.bind(this)} />
                  </FormField>
                  <FormField label="Email" error={errors[0]}>
                    <input type="email" name="email" value={user.email} onChange={this._onChange.bind(this)} />
                  </FormField>
                  <FormField label="Mobile Number" error={errors[0]}>
                    <input type="text" name="mobile" value={user.mobile} onChange={this._onChange.bind(this)} />
                  </FormField>
                </fieldset>

              </FormFields>

              <Footer pad={{vertical: 'medium'}}>
                <span />
                <Button type="submit" primary={true} label={this.localeData.user_edit_btn}
                  onClick={this._onSubmit.bind(this)} />
              </Footer>
            </Form>
          </Article>

        </Section>
      </Box>
      
    );
  }
}

UserEdit.contextTypes = {
  router: PropTypes.object
};

let select = (store) => {
  return {user: store.user, misc: store.misc};
};

export default connect(select)(UserEdit);
