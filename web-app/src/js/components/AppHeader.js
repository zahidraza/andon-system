import React, { Component } from 'react';
import { connect } from 'react-redux';
import { localeData } from '../reducers/localization';
import { navActivate} from '../actions/misc';
//import { USER_CONSTANTS as u} from '../utils/constants';

import Anchor from 'grommet/components/Anchor';
import Button from 'grommet/components/Button';
import Header from 'grommet/components/Header';
import Menu from 'grommet/components/Menu';
import MenuIcon from "grommet/components/icons/base/Menu";
import Title from 'grommet/components/Title';


class AppHeader extends Component {

  constructor () {
    super();
    this._openMenu = this._openMenu.bind(this);
  }

  componentWillMount () {
    this.setState({localeData: localeData()});
    
    if (sessionStorage.session == undefined) {
      this.context.router.push("/");
    }

  }

  _openMenu () {
    this.props.dispatch(navActivate(true));
  }

  _logout () {
    delete sessionStorage.access_token;
    delete sessionStorage.refresh_token;
    delete sessionStorage.email;
    delete sessionStorage.username;
    delete sessionStorage.role;
    delete sessionStorage.userType;
    delete sessionStorage.session;
  }

  render () {
    const { active: navActive} = this.props.nav;
    const { access_token, username} = window.sessionStorage;

    let title;
    if ( !navActive ) {
      title = (
        <Title>
          <Button icon={<MenuIcon />} onClick={this._openMenu} />
          {this.state.localeData.APP_NAME_FULL}
        </Title>
      );
    }else{
      title = (<Title>{this.state.localeData.APP_NAME_FULL}</Title>);
    }

    let login;
    if (access_token != undefined) {
      login = (
        <Menu direction="row" align="center" responsive={false}>
          <Anchor path="/profile">{username}</Anchor>
          <Anchor path="/" onClick={this._logout.bind(this)}>Logout</Anchor>
        </Menu>
      );
    }



    return (
      <Header size="large" justify="between" colorIndex="neutral-1-a" pad={{horizontal: "medium"}}>
        {title}
        {login}
      </Header>
    );
  }
}

AppHeader.contextTypes = {
  router: React.PropTypes.object.isRequired
};

let select = (store) => {
  return { nav: store.nav};
};

export default connect(select)(AppHeader);
