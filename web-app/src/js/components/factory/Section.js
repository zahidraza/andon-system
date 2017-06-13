import React, { Component } from 'react';
import { localeData } from '../../reducers/localization';
import { connect } from 'react-redux';
import {initialize} from '../../actions/misc';

import Box from 'grommet/components/Box';
import Header from 'grommet/components/Header';
import Sectn from 'grommet/components/Section';
import Spinning from 'grommet/components/icons/Spinning';
import Title from 'grommet/components/Title';
import Table from 'grommet/components/Table';
import TableRow from 'grommet/components/TableRow';

class Section extends Component {
  
  constructor () {
    super();
    this.state = {
      initializing: false
    };
    this.localeData = localeData();
  }

  componentWillMount () {
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

  render() {
    const {initializing} = this.state;
    const {sections} = this.props.misc;

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    let items = sections.map((section,index) => {
      return (
        <TableRow key={index}  >
          <td >{section}</td>
        </TableRow>
      );
    });

    return (
      <Box>
        <Header size='large' pad={{ horizontal: 'medium' }}>
          <Title responsive={false}>
            <span>{this.localeData.label_section}</span>
          </Title>
        </Header>
        <Sectn>
          <Box size="small" alignSelf="center" >
            <Table>
              <tbody>{items}</tbody>
            </Table>
          </Box>
        </Sectn>
      </Box>
    );
  }
}

Section.contextTypes = {
  router: React.PropTypes.object.isRequired
};

let select = (store) => {
  return {misc: store.misc};
};

export default connect(select)(Section);
