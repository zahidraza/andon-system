import React, {Component} from 'react';
import { connect } from 'react-redux';
import { localeData } from '../reducers/localization';

import AppHeader from './AppHeader';
import Box from 'grommet/components/Box';
import Section from 'grommet/components/Section';

class Dashboard extends Component {
  constructor () {
    super();
  }

  componentWillMount () {
    this.setState({localeData: localeData()});
  }

  render () {
    return (
      <Box>
        <AppHeader page={this.state.localeData.label_home} />
        <Section direction="column" pad={{vertical: 'large', horizontal:'small'}}>
          <h1>Welcome to Inventory Control System Application</h1>
        </Section>
      </Box>
    );
  }
}

Dashboard.contextTypes = {
  router: React.PropTypes.object.isRequired
};

let select = (store) => {
  return { nav: store.nav};
};

export default connect(select)(Dashboard);
