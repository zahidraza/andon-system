import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';
import { localeData } from '../../reducers/localization';
import {updateUser}  from '../../actions/user';
import {USER_CONSTANTS as c, USER_ROLE as ur, USER_LEVEL as ul, USER_TYPE as ut}  from '../../utils/constants';
import {initialize} from '../../actions/misc';

import Article from 'grommet/components/Article';
import Header from 'grommet/components/Header';
import Heading from 'grommet/components/Heading';
import Form from 'grommet/components/Form';
import Footer from 'grommet/components/Footer';
import FormFields from 'grommet/components/FormFields';
import FormField from 'grommet/components/FormField';
import Box from 'grommet/components/Box';
import Section from 'grommet/components/Section';
import Select from 'grommet/components/Select';
import Spinning from 'grommet/components/icons/Spinning';
import Button from 'grommet/components/Button';
import CloseIcon from 'grommet/components/icons/base/Close';
import Anchor from 'grommet/components/Anchor';

import AddIcon from "grommet/components/icons/base/Add";
import TrashIcon from 'grommet/components/icons/base/Trash';
import Layer from 'grommet/components/Layer';
import List from 'grommet/components/List';
import ListItem from 'grommet/components/ListItem';


class UserEdit extends Component {

  constructor () {
    super();

    this.state = {
      initializing: false,
      user: {},
      team: 'Select Team',
      teams: [],
      desgns: [],
      layer: {
        show: false,                             // name of layer under operation [section|supplier]
        title: 'Add Buyer',
        label: 'Buyers',
        filterValue: 'Select Buyer',
        filterItems: [],                    //Available items for selection
        selectedItems: [] 
      }
    };

    this.localeData = localeData();
    this._initDesgns = this._initDesgns.bind(this);
  }

  componentWillMount () {
    console.log('componentWillMount');
    if (!this.props.misc.initialized) {
      this.setState({initializing: true});
      this.props.dispatch(initialize());
    }else{
      let {team, teams, layer} = this.state;
      const {user} = this.props.user;
      const {buyers, desgns} = this.props.misc;
      this._initDesgns(desgns);
      teams = this.props.misc.teams;

      if ( 'buyers' in user && user.buyers.length != 0) {
        team = user.buyers[0].team;
        layer.selectedItems = user.buyers;
        layer.filterItems = buyers.filter(b => b.team == team).filter(b => ! user.buyers.map(b2 => b2.name).includes(b.name) ).map(b => b.name);
      }
      this.setState({teams, team, user, layer});
    }
  }

  componentWillReceiveProps (nextProps) {
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
      this.setState({user: nextProps.user.user});
      this._initDesgns(nextProps.misc.desgns);
    }
    if (!nextProps.user.editing) {
      this.context.router.push('/user');
    }
  }

  _initDesgns (designations) {
    let desgns = designations.map(d => d.name);
    this.setState({desgns});
  }

  _onSubmit (event) {
    event.preventDefault();
    let {user,layer} = this.state;
    
    if (user.level == ul.LEVEL1 || user.level == ul.LEVEL2) {
      user.buyers = layer.selectedItems;
    } else if (user.level == ul.LEVEL3) {
      user.buyers = this.props.misc.buyers;
    } else {
      user.buyers = [];
    }
    console.log(user);
    this.props.dispatch(updateUser(user));
  }

  _onChange (event) {
    let user = this.state.user;
    user[event.target.getAttribute('name')] = event.target.value;
    this.setState({user: user});
  }

  _onChangeAddress (event) {
    let user = this.state.user;
    user.address[event.target.getAttribute('name')] = event.target.value;
    this.setState({user: user});
  }

  _onFilter (event) {
    let {user} = this.state;
    user[event.target.getAttribute('name')] = event.value;
    if (event.value == ur.ROLE_ADMIN) {
      user.level = ul.LEVEL4;
    }
    if (event.value == ur.ROLE_USER && user.designation == null) {
      user.designation = 'Select Designation';
    }
    this.setState({user});
  }


  _onClose (event) {
    this.props.dispatch({type: c.USER_EDIT_FORM_TOGGLE, payload: {adding: false}});
  }

  
  _onLayerSubmit (event) {
    event.preventDefault();
    let {layer,team} = this.state;
    const {misc: {buyers}} = this.props;

    if (! layer.filterValue.includes('Select')) {   
      layer.filterItems = layer.filterItems.filter(b => b != layer.filterValue);
      layer.selectedItems = buyers.filter(b => b.team == team && !layer.filterItems.includes(b.name));
    }

    layer.filterValue = 'Select Buyer';
    layer.show = false;
    this.setState({layer: layer});
  }

  _onLayerClose () {
    let {layer} = this.state;
    layer.show = false;
    layer.filterValue = 'Select Buyer';
    this.setState({layer});
  }

  _onLayerSelect (event) {
    let {layer} = this.state;
    layer.filterValue = event.value;
    this.setState({layer: layer});
  }

  _onRemove (index,event) {
    let {layer} = this.state;
    let rItem = layer.selectedItems[index];
    layer.selectedItems = layer.selectedItems.filter(b => b.id != rItem.id);
    layer.filterItems.push(rItem.name);
    this.setState({layer: layer});
  }

  _onAdd (event) {   // On Section/Supplier Add Click. Show corresponding layer for adding.
    console.log('_onAdd');
    let {layer, team} = this.state;
    if (team.includes('Select')) {
      alert('First Select Team');
      return;
    }
    layer.show = true;
    this.setState({layer: layer});
  }

  _onTeamFilter (event) {
    let {layer} = this.state;
    const {buyers} = this.props.misc;

    const team = event.value;

    layer.filterItems = [];
    layer.selectedItems = buyers.filter(b => b.team == team);

    layer.filterValue = 'Select Buyer';
    layer.show = false;
    this.setState({layer,team});
  }

  _renderLayer () {
    const {layer} = this.state;

    let result;
    if (layer.show) {
      result = (
        <Layer align="right" closer={true} onClose={this._onLayerClose.bind(this)}
          a11yTitle={layer.title}>
          
          <Form onSubmit={this._onLayerSubmit.bind(this)} compact={false}>
            <Header>
              <Heading tag="h2" margin='none'>{layer.title}</Heading>
            </Header>
            <Box pad={{vertical: 'medium'}}/>
            <FormFields>
              <fieldset>
                <FormField htmlFor="name" label={layer.label} error=''>
                  <Select id="name" name="name"
                    value={layer.filterValue}
                    options={layer.filterItems}
                    onChange={this._onLayerSelect.bind(this)} />
                </FormField>
              </fieldset>
            </FormFields>
            <Footer pad={{vertical: 'medium'}}>
              <Button type="submit" primary={true} label="OK"
                onClick={this._onLayerSubmit.bind(this)} />
            </Footer>
          </Form>
        </Layer>
      );
    }
    return result;
  }

  _renderFields () {
    const {layer,teams,team,user} = this.state;

    if (! (user.level == ul.LEVEL1 || user.level == ul.LEVEL2) || user.userType == ut.FACTORY) {
      return null;
    }

    let selected = layer.selectedItems;

    const selectedFields = selected.map((buyer, index) => {
      return (
        <ListItem key={index} justify="between" pad="none"
          separator={index === 0 ? 'horizontal' : 'bottom'}
          responsive={false}>
          <span>{buyer.name}</span>
          <Button icon={<TrashIcon />}
            onClick={this._onRemove.bind(this,index)}
            a11yTitle={`Remove Section`} />
        </ListItem>
      );
    });

    return (
      <fieldset>
        <Header size="small" justify="between">
          <Heading tag="h3">{layer.label}</Heading>
          <Button icon={<AddIcon />} onClick={this._onAdd.bind(this)}
            a11yTitle={layer.title} />
        </Header>
        <FormField label="Team" htmlFor="team" error=''>
          <Select id="team" name="team" options={teams}
            value={team}  onChange={this._onTeamFilter.bind(this)} />
        </FormField>
        <List>
          {selectedFields}
        </List>
      </fieldset>
    );
  }



  render () {
    const {error,busy} = this.props.user;
    const {user,initializing, desgns} = this.state;

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    const busyIcon = busy ? <Spinning /> : null;

    const layerControl = this._renderLayer();

    const buyerFields = this._renderFields();

    const  levelFilter = (user.role == ur.ROLE_ADMIN || user.userType == ut.FACTORY) ? null : (
      <FormField label="User Level" htmlFor="level" error={error.level}>
        <Select id="level" name="level" options={[ul.LEVEL1, ul.LEVEL2, ul.LEVEL3]}
          value={user.level}  onChange={this._onFilter.bind(this)} />
      </FormField>
    );

    const  desgnFilter = (user.role == ur.ROLE_ADMIN || user.userType != ut.FACTORY )? null : (
      <FormField label="User Designation" htmlFor="level" error={error.level}>
        <Select id="desgn" name="designation" options={desgns}
          value={user.designation}  onChange={this._onFilter.bind(this)} />
      </FormField>
    );

    return (
      <Box>
        <Section>
          <Article align="center" pad={{horizontal: 'medium'}} primary={true}>
            <Form onSubmit={this._onSubmit}>

              <Header size="large" justify="between" pad="none">
                <Heading tag="h2" margin="none" strong={true}>{this.localeData.label_user_edit}</Heading>
                <Anchor icon={<CloseIcon />} path="/user" a11yTitle='Close Add User Form' onClick={this._onClose.bind(this)} />
              </Header>

              <FormFields>

                <fieldset>
                  <FormField label="User Role" htmlFor="sType" error={error.role}>
                    <Select id="role" name="role" options={[ur.ROLE_ADMIN, ur.ROLE_USER]}
                      value={user.role}  onChange={this._onFilter.bind(this)} />
                  </FormField>
                  {levelFilter}
                  {desgnFilter}
                  <FormField label="Full Name" error={error.name}>
                    <input type="text" name="name" value={user.name} onChange={this._onChange.bind(this)} />
                  </FormField>
                  <FormField label="Email/Username" error={error.email}>
                    <input type="email" name="email" value={user.email} onChange={this._onChange.bind(this)} />
                  </FormField>
                  <FormField label="Mobile Number" error={error.mobile}>
                    <input type="text" name="mobile" value={user.mobile} onChange={this._onChange.bind(this)} />
                  </FormField>
                </fieldset>

                {buyerFields}

              </FormFields>

              <Footer pad={{vertical: 'medium'}}>
                <span />
                <Button type="submit" primary={true} label={this.localeData.user_edit_btn}
                  icon={busyIcon} onClick={this._onSubmit.bind(this)} />
              </Footer>
            </Form>
          </Article>
          {layerControl}
        </Section>
      </Box>
      
    );
  }
}

UserEdit.contextTypes = {
  router: PropTypes.object.isRequired
};

let select = (store) => {
  return {user: store.user, misc: store.misc};
};

export default connect(select)(UserEdit);
