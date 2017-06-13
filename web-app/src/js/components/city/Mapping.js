import React, { Component } from 'react';
import { localeData } from '../../reducers/localization';
import { connect } from 'react-redux';
import {initialize} from '../../actions/misc';
import {USER_LEVEL as ul}  from '../../utils/constants';

import Box from 'grommet/components/Box';
import Header from 'grommet/components/Header';
import Section from 'grommet/components/Section';
import Spinning from 'grommet/components/icons/Spinning';
import Title from 'grommet/components/Title';
import Table from 'grommet/components/Table';
import TableRow from 'grommet/components/TableRow';
import TableHeader from 'grommet/components/TableHeader';

class Mapping extends Component {
  
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



    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    
    let items = buyers.map((buyer,index) => {
      let level1 = '  ';
      buyer.users.filter(u => u.level == ul.LEVEL1).forEach(user => level1 = level1 + user.name + ", ");
      let level2 = '  ';
      buyer.users.filter(u => u.level == ul.LEVEL2).forEach(user => level2 = level2 + user.name + ", ");
      let level3 = '  ';
      buyer.users.filter(u => u.level == ul.LEVEL3).forEach(user => level3 = level3 + user.name + ", ");
      return (
        <TableRow key={index}  >
          <td >{buyer.team}</td>
          <td >{buyer.name}</td>
          <td>{level1.substring(0,level1.length-2).trim()}</td>
          <td>{level2.substring(0,level2.length-2).trim()}</td>
          <td>{level3.substring(0,level3.length-2).trim()}</td>
        </TableRow>
      );
    });

    return (
      <Box>
        <Header size='large' pad={{ horizontal: 'medium' }}>
          <Title responsive={false}>
            <span>{this.localeData.label_buyer}</span>
          </Title>
        </Header>
        <Section>
          <Box full="horizontal" wrap={true} size='full'>
            <Table>
              <TableHeader labels={['Team','Buyer','Level 1', 'Level 2', 'Level 3']} />
              <tbody>{items}</tbody>
            </Table>
          </Box>
        </Section>
      </Box>
    );
  }
}

Mapping.contextTypes = {
  router: React.PropTypes.object.isRequired
};

let select = (store) => {
  return {misc: store.misc};
};

export default connect(select)(Mapping);
