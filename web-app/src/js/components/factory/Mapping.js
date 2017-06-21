import React, { Component } from 'react';
import { localeData } from '../../reducers/localization';
import { connect } from 'react-redux';
import {initialize} from '../../actions/misc';

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
import Tab from 'grommet/components/Tab';
import Tabs from 'grommet/components/Tabs';

class Mapping extends Component {
  
  constructor () {
    super();
    this.state = {
      initializing: false,
      viewing: false,
      mapping: [],
      item: {},
      activeTab: 0
    };
    this.localeData = localeData();
    this._loadMapping = this._loadMapping.bind(this);
    this._renderLayerView = this._renderLayerView.bind(this);
  }

  componentWillMount () {
    console.log('componentWillMount');
    if (!this.props.misc.initialized) {
      this.setState({initializing: true});
      this.props.dispatch(initialize());
    } else {
      const {misc: {departments, problms}, user: {users}} = this.props;
      this._loadMapping(departments, problms, users);
    }
  }

  componentWillReceiveProps (nextProps) {
    if (sessionStorage.session == undefined) {
      this.context.router.push('/');
    }
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
      const {misc: {departments, problms}, user: {users}} = nextProps;
      this._loadMapping(departments, problms, users);
    }
  }

  _loadMapping (departments, problms, users) {
    console.log('before filter: users = ' + users.length);
    //Filter users of factory
    users = users.filter(u => u.desgnId != null);
    console.log('after filter: users = ' + users.length);

    let mapping = [];
    departments.forEach((dept) => {
      let probs = problms.filter((p) => p.department == dept);

      probs.forEach((p) => {
        let level1 = [] , level2 = [] , level3 = [];
        let desgn1 = [] , desgn2 = [] , desgn3 = [];


        p.designations.filter(d => d.level == 1).forEach(desgn => {
          desgn1.push(desgn.name);
          users.filter(u => u.desgnId == desgn.id).forEach(usr => level1.push(usr.name));
        });

        p.designations.filter(d => d.level == 2).forEach(desgn => {
          desgn2.push(desgn.name);
          users.filter(u => u.desgnId == desgn.id).forEach(usr => level2.push(usr.name));
        });

        p.designations.filter(d => d.level == 3).forEach(desgn => {
          desgn3.push(desgn.name);
          users.filter(u => u.desgnId == desgn.id).forEach(usr => level3.push(usr.name));
        });
        mapping.push({dept, problem: p.name, level1, level2, level3, desgn1, desgn2, desgn3});
      });
    });
    this.setState({mapping});
  }
  _onTabChange (index) {
    this.setState({activeTab: index});
  }

  _onClick (index, event) {
    event.preventDefault();
    this.setState({viewing: true, item: this.state.mapping[index]});
  }

  _onCloseLayer () {
    this.setState({viewing: false});
  }

  _renderLayerView () {
    const {item,viewing,activeTab} = this.state;
    if (!viewing) return null;

    let level1, level2, level3;
    if (activeTab == 0) {
      level1 = item.level1.map((user,i) => {
        return (
          <ListItem  key={i} justify="end" pad={{vertical:'small',horizontal:'small'}} >
            {user}
          </ListItem>
        );
      });
      level2 = item.level2.map((user,i) => {
        return (
          <ListItem  key={i} justify="end" pad={{vertical:'small',horizontal:'small'}} >
            {user}
          </ListItem>
        );
      });
      level3 = item.level3.map((user,i) => {
        return (
          <ListItem  key={i} justify="end" pad={{vertical:'small',horizontal:'small'}} >
            {user}
          </ListItem>
        );
      });
    } else if (activeTab == 1) {
      level1 = item.desgn1.map((desgn,i) => {
        return (
          <ListItem  key={i} justify="end" pad={{vertical:'small',horizontal:'small'}} >
            {desgn}
          </ListItem>
        );
      });
      level2 = item.desgn2.map((desgn,i) => {
        return (
          <ListItem  key={i} justify="end" pad={{vertical:'small',horizontal:'small'}} >
            {desgn}
          </ListItem>
        );
      });
      level3 = item.desgn3.map((desgn,i) => {
        return (
          <ListItem  key={i} justify="end" pad={{vertical:'small',horizontal:'small'}} >
            {desgn}
          </ListItem>
        );
      });
    }
    
    
    return (
      <Layer hidden={!viewing}  onClose={this._onCloseLayer.bind(this)}  closer={true} align="center">
        <Box size="large"  pad={{vertical: 'none', horizontal:'small'}}>
          <Header><Heading tag="h3" strong={true} >Mapping Details</Heading></Header>
          <List>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Department: </span>
              <span className="secondary">{item.dept}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Problem: </span>
              <span className="secondary">{item.problem}</span>
            </ListItem>
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Level1 {activeTab == 0 ? 'Users:' : 'Designations:'}</span>
              <span className="secondary"/>
            </ListItem>
            {level1}
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Level2 {activeTab == 0 ? 'Users:' : 'Designations:'} </span>
              <span className="secondary"/>
            </ListItem>
            {level2}
            <ListItem justify="between" pad={{vertical:'small',horizontal:'small'}} >
              <span> Level3 {activeTab == 0 ? 'Users:' : 'Designations:'}</span>
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
    const {initializing, mapping, activeTab} = this.state;

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    let items1 = mapping.map((m,i) => {
      let l1 = '  ', l2 = '  ', l3 = '  ';
      m.level1.forEach(u => l1 = l1 + u + ", ");
      m.level2.forEach(u => l2 = l2 + u + ", ");
      m.level3.forEach(u => l3 = l3 + u + ", ");

      l1 = l1.substring(0,l1.length-2).trim();
      l2 = l2.substring(0,l2.length-2).trim();
      l3 = l3.substring(0,l3.length-2).trim();

      l1 = l1.length > 30 ? (<h6> {l1.substr(0,30)} <a href="#" onClick={this._onClick.bind(this, i)}>  more</a></h6>) : <h6> {l1} </h6>;
      l2 = l2.length > 30 ? (<h6> {l2.substr(0,30)} <a href="#" onClick={this._onClick.bind(this, i)}>  more</a></h6>) : <h6> {l2} </h6>;
      l3 = l3.length > 30 ? (<h6> {l3.substr(0,30)} <a href="#" onClick={this._onClick.bind(this, i)}>  more</a></h6>) : <h6> {l3} </h6>;

      return (
        <TableRow key={i++} >
          <td><h6>{m.dept}</h6></td>
          <td><h6>{m.problem}</h6></td>
          <td>{l1}</td>
          <td>{l2}</td>
          <td>{l3}</td>
        </TableRow>
      );
    });

    let items2 = mapping.map((m,i) => {
      let l1 = '  ', l2 = '  ', l3 = '  ';
      m.desgn1.forEach(d => l1 = l1 + d + ", ");
      m.desgn2.forEach(d => l2 = l2 + d + ", ");
      m.desgn3.forEach(d => l3 = l3 + d + ", ");

      l1 = l1.substring(0,l1.length-2).trim();
      l2 = l2.substring(0,l2.length-2).trim();
      l3 = l3.substring(0,l3.length-2).trim();

      l1 = l1.length > 30 ? (<h6> {l1.substr(0,30)} <a href="#" onClick={this._onClick.bind(this, i)}>  more</a></h6>) : <h6> {l1} </h6>;
      l2 = l2.length > 30 ? (<h6> {l2.substr(0,30)} <a href="#" onClick={this._onClick.bind(this, i)}>  more</a></h6>) : <h6> {l2} </h6>;
      l3 = l3.length > 30 ? (<h6> {l3.substr(0,30)} <a href="#" onClick={this._onClick.bind(this, i)}>  more</a></h6>) : <h6> {l3} </h6>;

      return (
        <TableRow key={i++} >
          <td><h6>{m.dept}</h6></td>
          <td><h6>{m.problem}</h6></td>
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
            <Tabs justify="center" activeIndex={activeTab} onActive={this._onTabChange.bind(this)} >
              <Tab title="User Mapping">
                <Table>
                  <TableHeader labels={['Department','Problem','Level1', 'Level2', 'Level3']} />
                  <tbody>{items1}</tbody>
                </Table>
              </Tab>
              <Tab title="Designation Mapping">
                <Table>
                  <TableHeader labels={['Department','Problem','Level1', 'Level2', 'Level3']} />
                  <tbody>{items2}</tbody>
                </Table>
              </Tab>
            </Tabs>
          </Box>
        </Section>
        {layerView}
      </Box>
    );
  }
}

Mapping.contextTypes = {
  router: React.PropTypes.object
};

let select = (store) => {
  return {misc: store.misc, user: store.user};
};

export default connect(select)(Mapping);
