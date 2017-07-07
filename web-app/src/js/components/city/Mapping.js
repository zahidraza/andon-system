import React, { Component } from 'react';
import { localeData } from '../../reducers/localization';
import { connect } from 'react-redux';
import {initialize} from '../../actions/misc';
import {USER_LEVEL as ul}  from '../../utils/constants';

import Box from 'grommet/components/Box';
import Header from 'grommet/components/Header';
import Heading from 'grommet/components/Heading';
import Section from 'grommet/components/Section';
import Spinning from 'grommet/components/icons/Spinning';
import Title from 'grommet/components/Title';
import Table from 'grommet/components/Table';
import TableRow from 'grommet/components/TableRow';
import TableHeader from 'grommet/components/TableHeader';
import List from 'grommet/components/List';
import ListItem from 'grommet/components/ListItem';
import Layer from 'grommet/components/Layer';

class Mapping extends Component {
  
  constructor () {
    super();
    this.state = {
      initializing: false,
      viewing: false,
      item: {}
    };
    this.localeData = localeData();
    this._renderLayerView = this._renderLayerView.bind(this);
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

  _onHelpClick () {

  }

  _onClick (index, event) {
    event.preventDefault();
    this.setState({viewing: true, item: this.props.misc.buyers[index]});
  }

  _onCloseLayer () {
    this.setState({viewing: false});
  }

  _renderLayerView () {
    const {item,viewing} = this.state;
    if (!viewing) return null;

    let level1, level2, level3;
    level1 = item.users.filter(u => u.level == ul.LEVEL1).map((user,i) => {
      return (
        <ListItem  key={i} justify="end" pad={{vertical:'small',horizontal:'small'}} >
          {user.name}
        </ListItem>
      );
    });
    level2 = item.users.filter(u => u.level == ul.LEVEL2).map((user,i) => {
      return (
        <ListItem  key={i} justify="end" pad={{vertical:'small',horizontal:'small'}} >
          {user.name}
        </ListItem>
      );
    });
    level3 = item.users.filter(u => u.level == ul.LEVEL3).map((user,i) => {
      return (
        <ListItem  key={i} justify="end" pad={{vertical:'small',horizontal:'small'}} >
          {user.name}
        </ListItem>
      );
    });
    
    
    
    return (
      <Layer hidden={!viewing}  onClose={this._onCloseLayer.bind(this)}  closer={true} align="center">
        <Box size="large"  pad={{vertical: 'none', horizontal:'small'}}>
          <Header><Heading tag="h3" strong={true} >Mapping Details</Heading></Header>
          <List>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Team: </span>
              <span className="secondary">{item.team}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Buyer: </span>
              <span className="secondary">{item.name}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Level1 Users:</span>
              <span className="secondary"/>
            </ListItem>
            {level1}
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Level2 Users: </span>
              <span className="secondary"/>
            </ListItem>
            {level2}
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Level3 Users:</span>
              <span className="secondary"/>
            </ListItem>
            {level3}
          </List>
        </Box>
        <Box pad={{vertical: 'medium', horizontal:'small'}} />
      </Layer>
    );
  }

  render() {
    const {initializing} = this.state;
    let {buyers} = this.props.misc;

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    buyers = buyers.sort((a,b) => {
      return (b.team < a.team) ? -1 : 1;
    });
    let items = buyers.map((buyer,index) => {
      let l1 = '  ';
      buyer.users.filter(u => u.level == ul.LEVEL1).forEach(user => l1 = l1 + user.name + ", ");
      let l2 = '  ';
      buyer.users.filter(u => u.level == ul.LEVEL2).forEach(user => l2 = l2 + user.name + ", ");
      let l3 = '  ';
      buyer.users.filter(u => u.level == ul.LEVEL3).forEach(user => l3 = l3 + user.name + ", ");

      l1 = l1.substring(0,l1.length-2).trim();
      l1 = l1.length > 30 ? (<h6> {l1.substr(0,30)} <a href="#" onClick={this._onClick.bind(this, index)}>  more</a></h6>) : <h6> {l1} </h6>;
      l2 = l2.substring(0,l2.length-2).trim();
      l2 = l2.length > 30 ? (<h6> {l2.substr(0,30)} <a href="#" onClick={this._onClick.bind(this, index)}>  more</a></h6>) : <h6> {l2} </h6>;
      l3 = l3.substring(0,l3.length-2).trim();
      l3 = l3.length > 30 ? (<h6> {l3.substr(0,30)} <a href="#" onClick={this._onClick.bind(this, index)}>  more</a></h6>) : <h6> {l3} </h6>;
      return (
        <TableRow key={index}  >
          <td >{buyer.team}</td>
          <td >{buyer.name}</td>
          <td>{l1}</td>
          <td>{l2}</td>
          <td>{l3}</td>
        </TableRow>
      );
    });

    const layerView = this._renderLayerView();

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
        {layerView}
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
