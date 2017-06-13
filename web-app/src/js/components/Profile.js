import React, { Component } from 'react';
import { localeData } from '../reducers/localization';
import { connect } from 'react-redux';
import {initialize} from '../actions/misc';
import {changePassword} from '../actions/user';
import {USER_CONSTANTS as c,USER_TYPE as ut}  from '../utils/constants';

import Box from 'grommet/components/Box';
import Button from 'grommet/components/Button';
import Footer from 'grommet/components/Footer';
import Section from 'grommet/components/Section';
import Spinning from 'grommet/components/icons/Spinning';
import List from 'grommet/components/List';
import ListItem from 'grommet/components/ListItem';
import Form from 'grommet/components/Form';
import FormField from 'grommet/components/FormField';
import FormFields from 'grommet/components/FormFields';
import Layer from 'grommet/components/Layer';
import Header from 'grommet/components/Header';
import Heading from 'grommet/components/Heading';

class Profile extends Component {
  
  constructor () {
    super();
    this.state = {
      initializing: false,
      changingPassword: false,
      user: {},
      credential: {},
      errors: []
    };
    this.localeData = localeData();
    this._renderLayer = this._renderLayer.bind(this);
  }

  componentWillMount () {
    console.log('componentWillMount');
    if (!this.props.misc.initialized) {
      this.setState({initializing: true});
      this.props.dispatch(initialize());
    } else {
      const {users} = this.props.user;
      let i = users.findIndex(u => u.email == sessionStorage.email);
      const user = users[i];
      this.setState({user});
    }
  }

  componentWillReceiveProps (nextProps) {
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
      const {users} = this.props.user;
      let i = users.findIndex(u => u.email == sessionStorage.email);
      const user = users[i];
      this.setState({user});
    }
    if (nextProps.user.message != '') {
      alert(nextProps.user.message);
      this.props.dispatch({type: c.USER_CHANGE_PASSWD, payload: {message: ''}});
    }
  }

  _changePassword () {
    console.log("_changePassword");
    let {credential, user} = this.state;
    let errors = [];
    if (credential.oldPassword == undefined || credential.oldPassword == '') {
      errors[0] = 'Old Password cannot be blank.';
    }
    if (credential.newPassword == undefined || credential.newPassword == '') {
      errors[1] = 'New Password cannot be blank.';
    }
    if (credential.confirmNewPassword == undefined || credential.confirmNewPassword == '') {
      errors[2] = 'Confirm New Password cannot be blank.';
    }
    if (credential.newPassword != credential.confirmNewPassword) {
      errors[2] = 'Passwords do not match.';
    }
    this.setState({errors: errors});
    if (errors.length != 0) {
      return;
    }

    credential.email = user.email;
    this.props.dispatch(changePassword(credential));
    this.setState({changingPassword: false, credential: {}});
  }

  _onClick () {
    this.setState({changingPassword: true});
  }

  _onCloseLayer () {
    this.setState({changingPassword: false});
  }

  _onChange (event) {
    let { credential, errors } = this.state;
    if (event.target.getAttribute('name') == 'confirmNewPassword') {
      const confirmNewPassword = event.target.value;
      if (confirmNewPassword != credential.newPassword) {
        errors[2] = "Passwords do not match.";
      } else {
        errors[2] = '';
      }
    }
    credential[event.target.getAttribute('name')] = event.target.value;
    this.setState({credential: credential, errors: errors});
  }


  _renderLayer (changingPassword) {
    if (! changingPassword) {
      return null;
    }

    const {credential,errors} = this.state;

    return (
      <Layer onClose={this._onCloseLayer.bind(this)}  closer={true} align="center">
        <Form>
          <Header><Heading tag="h3" strong={true}>Change Password</Heading></Header>
          {/*<h3 style={{color: 'red'}}>{errorMessage}</h3>*/}
          <FormFields >
            <FormField label="Old Password" error={errors[0]}>
              <input type="password" name="oldPassword" value={credential.oldPassword} onChange={this._onChange.bind(this)} />
            </FormField>
            <FormField label="New Password" error={errors[1]}>
              <input type="password" name="newPassword" value={credential.newPassword} onChange={this._onChange.bind(this)} />
            </FormField>
            <FormField label="Confirm New Password" error={errors[2]}>
              <input type="password" name="confirmNewPassword" value={credential.confirmNewPassword} onChange={this._onChange.bind(this)} />
            </FormField>
          </FormFields>
          <Footer pad={{"vertical": "medium"}} >
            <Button label="Save" primary={true}  onClick={this._changePassword.bind(this)} />
          </Footer>
        </Form>
      </Layer>
    );

  }

  render() {
    const {initializing, user, changingPassword} = this.state;

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    const layerChangePassword = this._renderLayer(changingPassword);

    const userLevel = user.userType != ut.SAMPLING ? null : (
      <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
        <span> User Level </span>
        <span className="secondary">{user.level}</span>
      </ListItem>
    ); 

    return (
      <Box>
        <Section>
          <Box size="large" alignSelf="center">
            <Box pad={{vertical: 'medium'}}>
              <List>
                <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
                  <span> User Name </span>
                  <span className="secondary">{user.name}</span>
                </ListItem>
                <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
                  <span> Email Id </span>
                  <span className="secondary">{user.email}</span>
                </ListItem>
                <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
                  <span> Mobile </span>
                  <span className="secondary">{user.mobile}</span>
                </ListItem>
                <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
                  <span> Role </span>
                  <span className="secondary">{user.role}</span>
                </ListItem>
                {userLevel}
              </List>
            </Box>
            <Box alignSelf="center"><Button label="Change Password" onClick={this._onClick.bind(this)} /></Box>
          </Box>
        </Section>
        {layerChangePassword}
      </Box>
    );
  }
}

Profile.contextTypes = {
  router: React.PropTypes.object.isRequired
};

let select = (store) => {
  return {misc: store.misc, user: store.user};
};

export default connect(select)(Profile);
