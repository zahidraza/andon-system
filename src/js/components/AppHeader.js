import React, { Component } from 'react';
import { connect } from 'react-redux';
import { localeData } from '../reducers/localization';

import Button from 'grommet/components/Button';

import Header from 'grommet/components/Header';


import MenuIcon from "grommet/components/icons/base/Menu";

import Title from 'grommet/components/Title';

import { navActivate} from '../actions';

class AppHeader extends Component {

  constructor () {
    super();
    this._openMenu = this._openMenu.bind(this);
  }

  componentWillMount () {
    this.setState({localeData: localeData()});
  }

  _openMenu () {
    this.props.dispatch(navActivate(true));
  }

  render () {
    const { active: navActive} = this.props.nav;

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

    return (
      <Header size="large" justify="between" colorIndex="neutral-1-a" pad={{horizontal: "medium"}}>
        {title}
      </Header>
    );
  }
}

let select = (store) => {
  return { nav: store.nav};
};

export default connect(select)(AppHeader);
