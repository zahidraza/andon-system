import React, { Component } from 'react';
import { localeData } from '../../reducers/localization';
import { connect } from 'react-redux';
import {initialize} from '../../actions/misc';

import AppHeader from '../AppHeader';
import Box from 'grommet/components/Box';
import Header from 'grommet/components/Header';
import Section from 'grommet/components/Section';
import Spinning from 'grommet/components/icons/Spinning';
import Title from 'grommet/components/Title';
import Table from 'grommet/components/Table';
import TableRow from 'grommet/components/TableRow';
import TableHeader from 'grommet/components/TableHeader';

class Buyer extends Component {
  
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
  }

  componentWillReceiveProps (nextProps) {
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
    }
  }

  _onHelpClick () {

  }

  render() {
    const {initializing} = this.state;
    const {buyers} = this.props.misc;

    let items = buyers.map((buyer,index) => {
      return (
        <TableRow key={index}  >
          <td >{buyer.Buyer}</td>
          <td >{buyer.name}</td>
        </TableRow>
      );
    });


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
        <AppHeader page={this.localeData.label_test}/>
        <Header size='large' pad={{ horizontal: 'medium' }}>
          <Title responsive={false}>
            <span>{this.localeData.label_buyer}</span>
          </Title>
        </Header>
        <Section>
          <Box size="large" alignSelf="center" >
            <Table>
              <TableHeader labels={['Buyer','Buyer']} />
              <tbody>{items}</tbody>
            </Table>
          </Box>
        </Section>
      </Box>
    );
  }
}

Buyer.contextTypes = {
  router: React.PropTypes.object.isRequired
};

let select = (store) => {
  return {misc: store.misc};
};

export default connect(select)(Buyer);
