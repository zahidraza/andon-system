import React, { Component } from "react";
import { connect } from 'react-redux';
import { localeData } from '../reducers/localization';

import Sidebar from "grommet/components/Sidebar";
import Header from "grommet/components/Header";
import Title from "grommet/components/Title";
import Button from "grommet/components/Button";
import Menu from "grommet/components/Menu";
import Close from "grommet/components/icons/base/Close";
import Anchor from 'grommet/components/Anchor';

import { navActivate } from '../actions';

class NavSidebar extends Component {

  constructor () {
    super();
    this._onClose = this._onClose.bind(this);
  }

  componentWillMount () {
    this.setState({localeData: localeData()});
  }

  _onClose () {
    console.log('Close Button clicked');
    this.props.dispatch(navActivate(false));
  }

  render () {
    const { items } = this.props.nav;
    var links = items.map( (page, index) => {
      var value = (page.path == this.props.routePath) ? 'active' : '';
      return (
        <Anchor className={value} key={page.label} path={page.path} label={page.label} />
      );
    });
    return (
      <Sidebar colorIndex="neutral-1" size="small">
        <Header pad="medium" justify="between" >
          <Title>{this.state.localeData.APP_NAME_SHORT}</Title>
          <Button icon={<Close />} onClick={this._onClose} />
        </Header>
        <Menu fill={true} primary={true}>
          {links}
        </Menu>
      </Sidebar>
    );
  }
}

let select = (store) => {
  return { nav : store.nav};
};

export default connect(select)(NavSidebar);
