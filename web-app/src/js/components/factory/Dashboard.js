import React, { Component } from 'react';
import { localeData } from '../../reducers/localization';
import { connect } from 'react-redux';
import {initialize} from '../../actions/misc';

import Box from 'grommet/components/Box';
import Section from 'grommet/components/Section';
import Spinning from 'grommet/components/icons/Spinning';

class Dashboard extends Component {
  
  constructor () {
    super();
    this.state = {
      initializing: false
    };
    this.localeData = localeData();
  }

  componentWillMount () {
    console.log('componentWillMount');
    if (!this.props.misc.initialized) {
      this.setState({initializing: true});
      this.props.dispatch(initialize());
    }
    console.log(new Date(1492108200000));
  }

  componentWillReceiveProps (nextProps) {
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
    }
  }

  render() {
    const {initializing} = this.state;

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    return (
      <Box>
        <Section>
          <h1>Dashboard Navigation page</h1>
        </Section>
      </Box>
    );
  }
}

Dashboard.contextTypes = {
  router: React.PropTypes.object
};

let select = (store) => {
  return {misc: store.misc};
};

export default connect(select)(Dashboard);
