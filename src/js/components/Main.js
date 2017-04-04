import React, { Component } from "react";
import { connect } from 'react-redux';

//components
import App from "grommet/components/App";
import Split from 'grommet/components/Split';

import NavSidebar from "./NavSidebar";


class Main extends Component {
  render () {
    const { active } = this.props.nav;

    var pane1 = active ? <NavSidebar routePath={this.props.children.props.location.pathname} /> : null;
    var pane2 = this.props.children;

    return (
      <App centered={false}>
        <Split flex="right">
          {pane1}
          {pane2}
        </Split>
      </App>
    );
  }
}

let select = (store) => {
  return {nav: store.nav};
};

export default connect(select)(Main);
