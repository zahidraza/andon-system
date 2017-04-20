import React, { Component } from 'react';
import { connect } from 'react-redux';
//import { localeData } from '../reducers/localization';
import {USER_CONSTANTS as c, USER_TYPE as ut, USER_LEVEL as ul}  from '../../utils/constants';

//import Box from 'grommet/components/Box';
import Button from 'grommet/components/Button';
import CloseIcon from 'grommet/components/icons/base/Close';
import Header from 'grommet/components/Header';
import Heading from 'grommet/components/Heading';
import Layer from 'grommet/components/Layer';
import Section from 'grommet/components/Section';
import Select from 'grommet/components/Select';
import Sidebar from 'grommet/components/Sidebar';
import Sort from 'grommet-addons/components/Sort';

class UserFilter extends Component {
  
  constructor () {
    super();
    this.state = {
      levels: []
    };
  }

  componentWillMount () {
    let levels = [];
    if (sessionStorage.userType == ut.SAMPLING) {
      levels = [ul.LEVEL0,ul.LEVEL4];
    }else if (sessionStorage.userType == ut.MERCHANDISING) {
      levels = [ul.LEVEL1, ul.LEVEL2, ul.LEVEL3, ul.LEVEL4];
    }

    let list = [];
    list.push({label: 'All', value: undefined});
    levels.forEach(l => list.push({label: l, value: l}));
    this.setState({levels: list});
  }

  _onChange (name,event) {
    let filter = this.props.user.filter;

    if (!event.option.value) {
      // user selected the 'All' option, which has no value, clear filter
      delete filter[name];
    } else {
      // we get the new option passed back as an object,
      // normalize it to just a value
      let selectedFilter = event.value.map(value => (
        typeof value === 'object' ? value.value : value)
      );
      console.log(selectedFilter);
      filter[name] = selectedFilter;
      if (filter[name].length === 0) {
        delete filter[name];
      }
    }
    this.props.dispatch({type:c.USER_FILTER, payload: {filter: filter}});
  }

  _onChangeSort (sort) {
    let sortString = `${sort.value}:${sort.direction}`;
    console.log(sortString);
    this.props.dispatch({type:c.USER_SORT, payload: {sort: sortString}});
  }


  render() {
    const {filter,sort} = this.props.user;

    const [sortProperty, sortDirection] = sort.split(':');

    return (
      <Layer align='right' flush={true} closer={false}
        a11yTitle='User Filter'>
        <Sidebar size='large'>
          <div>
            <Header size='large' justify='between' align='center'
              pad={{ horizontal: 'medium', vertical: 'medium' }}>
              <Heading tag='h2' margin='none'>Filter</Heading>
              <Button icon={<CloseIcon />} plain={true}
                onClick={this.props.onClose} />
            </Header>
            <Section pad={{ horizontal: 'large', vertical: 'small' }}>
              <Heading tag='h3'>Level</Heading>
              <Select inline={true} multiple={true} options={this.state.levels} value={filter.level} onChange={this._onChange.bind(this,'level')} />
            </Section>
            <Section pad={{ horizontal: 'large', vertical: 'small' }}>
              <Heading tag='h2'>Sort</Heading>
              <Sort options={[
                { label: 'User Name', value: 'name', direction: 'asc' }
              ]} value={sortProperty} direction={sortDirection}
              onChange={this._onChangeSort.bind(this)} />
            </Section>
          </div>
        </Sidebar>
      </Layer>
    );
  }
}

let select = (store) => {
  return { user: store.user};
};

export default connect(select)(UserFilter);
