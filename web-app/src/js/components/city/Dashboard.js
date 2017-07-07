import React, {Component} from 'react';
import { connect } from 'react-redux';
import { localeData } from '../../reducers/localization';
import {initialize} from '../../actions/misc';
import {getHeaders} from '../../utils/restUtil';
import axios from "axios";

import Box from 'grommet/components/Box';
import Select from 'grommet/components/Select';
import Spinning from 'grommet/components/icons/Spinning';
import AnnotatedMeter from 'grommet-addons/components/AnnotatedMeter';
import Heading from 'grommet/components/Heading';
import Legend from 'grommet/components/Legend';
import RadioButton from 'grommet/components/RadioButton';
import Layer from 'grommet/components/Layer';
import FilterIcon from 'grommet/components/icons/base/Filter';

class Dashboard extends Component {
  constructor () {
    super();
    this.state = {
      initializing: false,
      busy: true,
      team: {
        loaded: false,
        elements: {},
        type: 'minute',
        weeks: 1
      },
      problem: {
        loaded: false,
        elements: {},
        type: 'minute',
        weeks: 1
      },
      buyer: {
        loaded: false,
        elements: {},
        type: 'minute',
        weeks: 1
      },
      buyerTop5: {
        loaded: false,
        elements: {},
        type: 'minute',
        weeks: 1
      },
      filter: {
        show: false,
        items: [],
        selected: ''
      }
    };
    this.localeData = localeData();
    this._renderTile = this._renderTile.bind(this);
    this._getDowntimeTeamwise = this._getDowntimeTeamwise.bind(this);
    this._getDowntimeProblemwise = this._getDowntimeProblemwise.bind(this);
    this._getDowntimeBuyerwise = this._getDowntimeBuyerwise.bind(this);
    this._getDowntimeTop5Buyer = this._getDowntimeTop5Buyer.bind(this);
    this._renderFilterLayer = this._renderFilterLayer.bind(this);
  }

  componentWillMount () {
    console.log('componentWillMount');
    if (!this.props.misc.initialized) {
      this.setState({initializing: true});
      this.props.dispatch(initialize());
    }
    const {team,problem,buyer,buyerTop5} = this.state;
    this._getDowntimeTeamwise(team.weeks);
    this._getDowntimeProblemwise(problem.weeks);
    this._getDowntimeBuyerwise(buyer.weeks);
    this._getDowntimeTop5Buyer(buyerTop5.weeks);
  }

  componentWillReceiveProps (nextProps) {
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
    }
  }

  _getDowntimeTeamwise (weeks) {
    let {team, problem, buyer, buyerTop5} = this.state;
    let after = new Date().getTime();
    after = after - (weeks*7*24*60*60*1000);
    axios.get(window.serviceHost + '/v2/issues/downtime/byTeam?after=' + after, {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        team.elements = response.data;
        team.loaded = true;
        let busy = true;
        if (problem.loaded && buyer.loaded && buyerTop5.loaded) busy = false;
        this.setState({team, busy});
      }
    }).catch( (err) => {
      console.log(err);
    });
  }

  _getDowntimeProblemwise (weeks) {
    let {team, problem, buyer, buyerTop5} = this.state;
    let after = new Date().getTime();
    after = after - (weeks*7*24*60*60*1000);
    axios.get(window.serviceHost + '/v2/issues/downtime/byProblem?after=' + after, {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        problem.elements = response.data;
        problem.loaded = true;
        let busy = true;
        if (team.loaded && buyer.loaded && buyerTop5.loaded) busy = false;
        this.setState({problem, busy});
      }
    }).catch( (err) => {
      console.log(err);
    });
  }

  _getDowntimeBuyerwise (weeks) {
    let {team, problem, buyer, buyerTop5, filter} = this.state;
    let after = new Date().getTime();
    after = after - (weeks*7*24*60*60*1000);
    axios.get(window.serviceHost + '/v2/issues/downtime/byBuyer?after=' + after, {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        buyer.elements = response.data;
        buyer.loaded = true;
        const teams = Object.keys(buyer.elements);
        filter.selected = teams[0];
        filter.items = teams;
        let busy = true;
        if (problem.loaded && team.loaded && buyerTop5.loaded) busy = false;
        this.setState({buyer,filter, busy});
      }
    }).catch( (err) => {
      console.log(err);
    });
  }

  _getDowntimeTop5Buyer (weeks) {
    let {team, problem, buyer, buyerTop5} = this.state;
    let after = new Date().getTime();
    after = after - (weeks*7*24*60*60*1000);
    axios.get(window.serviceHost + '/v2/issues/downtime/byBuyer?top5=true&after=' + after, {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        buyerTop5.elements = response.data;
        buyerTop5.loaded = true;
        let busy = true;
        if (problem.loaded && buyer.loaded && team.loaded) busy = false;
        this.setState({buyerTop5, busy});
      }
    }).catch( (err) => {
      console.log(err);
    });
  }

  _onTypeChange (type, what) {
    let {team, problem, buyer, buyerTop5} = this.state;
    if (what == 'team') {
      team.type = type;
    }else if(what == 'problem') {
      problem.type = type;
    }else if(what == 'buyer') {
      buyer.type = type;
    } else if (what == 'buyerTop5') {
      buyerTop5.type = type;
    }
    this.setState({team, problem, buyer, buyerTop5});
  }

  _onWeekChange (weeks, what) {
    let {team, problem, buyer,buyerTop5} = this.state;
    if (what == 'team') {
      team.weeks = weeks;
      this._getDowntimeTeamwise(weeks);
    }else if(what == 'problem') {
      problem.weeks = weeks;
      this._getDowntimeProblemwise(weeks);
    }else if(what == 'buyer') {
      buyer.weeks = weeks;
      this._getDowntimeBuyerwise(weeks);
    }else if(what == 'buyerTop5') {
      buyerTop5.weeks = weeks;
      this._getDowntimeTop5Buyer(weeks);
    }
    this.setState({team, problem, buyer, buyerTop5, busy: true});
  }

  _onClose () {
    let filter = this.state.filter;
    filter.show = false;
    this.setState({filter});
  }

  _onFilterActive (params) {
    let filter = this.state.filter;
    filter.show = true;
    this.setState({filter});
  }

  _onTeamSelect (event) {
    let {filter} = this.state;
    
    let value = event.value;
    filter.selected = value;
    this.setState({filter});
    console.log(event);
  }

  _renderFilterLayer () {
    let {filter} = this.state;
    return (
      <Layer hidden={!filter.show} closer={true} onClose={this._onClose.bind(this)}>
        <Box margin='medium'>
          <Box><h3>Select Team</h3></Box>
          <Box margin='small'>
            <Select options={filter.items}
            value={filter.selected}
            onChange={this._onTeamSelect.bind(this)}/>
          </Box>
        </Box>
      </Layer>
    );
  }

  /**
   * title: Title of page
   * type: Percentage | Hours
   * weeks: 1 = Last 1 week, 4 = Last 1 month, 12 = Last 3 months
   * elements: array of elements to be shown
   * what: Tile for what = teamwise | problemwise | buyerwise
   * max: sum total of values
   */
  _renderTile (title, type, weeks, elements, what, max, filter) {
    if (type == 'percent') {
      elements = elements.map(e => {
        return {...e, value: +((e.value/max)*100).toFixed(2)};
      });
      max = 100;
    }

    if (true) {
      return (
        <Box flex={true} direction='column' full='horizontal' separator='all' pad={{vertical: 'small', horizontal: 'small'}} margin='small'>
          <Box direction='column'>
            <Box pad={{vertical: 'small'}} direction='row' justify='between'>
              <Box direction='row'>
                <Box pad={{horizontal: 'small'}}>{title}</Box>
                {filter != null ? <Box pad={{horizontal: 'small'}}>{filter.selected}</Box>: null}
                {filter != null ? <Box pad={{horizontal: 'small'}}><FilterIcon onClick={this._onFilterActive.bind(this)}/></Box>: null}
              </Box>
              <Box direction='row'>
                <RadioButton label='Minutes' checked={type == 'minute'}
                  id={what + '-type-2'} name={what + '-type-2'} onChange={this._onTypeChange.bind(this,'minute', what)}/>
                <RadioButton label='Percentage' checked={type == 'percent'}
                  id={what + '-type-1'} name={what + '-type-1'} onChange={this._onTypeChange.bind(this,'percent', what)}/>
              </Box>  
            </Box>
            <Box pad={{vertical: 'small'}}>
              <Box direction='row' justify='end'>
                <RadioButton label='Last 1 Week' checked={weeks == 1}
                  id={what + '-time-1'} name={what + '-time-1'} onChange={this._onWeekChange.bind(this,1, what)}/>
                <RadioButton label='Last 1 month' checked={weeks == 4}
                  id={what + '-time-2'} name={what + '-time-2'} onChange={this._onWeekChange.bind(this,4, what)}/>
                <RadioButton label='Last 3 months' checked={weeks == 12}
                  id={what + '-time-3'} name={what + '-time-3'} onChange={this._onWeekChange.bind(this,12, what)}/>
              </Box> 
            </Box>
          </Box>
          <Box  direction='row' justify='between'>
            <Box >
              <AnnotatedMeter
                type='circle'
                size='small'
                units={type == 'percent' ? '%' : 'mins'}
                max={max}
                series={elements} />
            </Box>
            <Box justify='center'>
              <Legend total={true} size='medium' units={type == 'percent' ? '%' : 'mins'}
                series={elements}/>
            </Box>
          </Box>
        </Box>
      );
    }
  } 

  render () {
    const {initializing, busy, team, problem, buyer, buyerTop5, filter} = this.state;
    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }
    let teams = [];
    let max1 = 0;
    Object.keys(team.elements).forEach((key, i)=> {
      let minute = Math.round(team.elements[key]);
      max1 += minute;
      let color = (i < 4) ? 'neutral-' + (i+1) : 'accent-' + (i-4+1);
      teams.push({"label": key, "value": minute, "colorIndex": color});
    });

    let problems = [];
    let max2 = 0;
    Object.keys(problem.elements).forEach((key, i)=> {
      let minute = Math.round(problem.elements[key]);
      max2 += minute;
      problems.push({"label": key, "value": minute, "colorIndex": "graph-"+(i+1)});
    });

    let buyers = [];
    let teamBuyer;
    teamBuyer = buyer.elements[filter.selected];
    
    let max3 = 0;
    if (teamBuyer != undefined) {
      Object.keys(teamBuyer).forEach((key,i) => {
        let minute = Math.round(teamBuyer[key]);
        max3 += minute;
        let color = (i < 4) ? 'neutral-' + (i+1) :( (i < 6) ?  'accent-' + (i-4+1) : 'grey-' + (i-6+1));
        buyers.push({"label": key, "value": minute, "colorIndex": color});
      });
    }

    let buyersTop5 = [];
    let max4 = 0;
    Object.keys(buyerTop5.elements).forEach((key,i) => {
      max4 += buyerTop5.elements[key];
      let color = (i < 4) ? 'neutral-' + (i+1) : 'accent-' + (i-4+1);
      if (key != 'Others') {
        buyersTop5.push({"label":key, "value": buyerTop5.elements[key],"colorIndex": color });
      }
    });
    buyersTop5.sort((a,b) => b.value - a.value);
    if (buyerTop5.loaded) {
      buyersTop5.push({"label":'Others', "value": buyerTop5.elements.Others,"colorIndex": 'light-2'});
    } 
    let teamTile = this._renderTile("Downtime (Team wise)", team.type, team.weeks, teams,'team', max1, null);
    let problemTile = this._renderTile("Downtime (Problem wise)", problem.type, problem.weeks, problems, 'problem', max2, null);
    let buyerTile = this._renderTile("Downtime (Buyer wise)", buyer.type, buyer.weeks, buyers, 'buyer', max3, filter);
    let buyerTop5Tile = this._renderTile("Downtime (Top 5 Buyer)", buyerTop5.type, buyerTop5.weeks, buyersTop5, 'buyerTop5', max4,null);
    const teamFilterLayer = this._renderFilterLayer();
    return (
      <Box >
        <Box direction='row' margin='small' justify='between'>
          <Box>
            <Heading tag='h3' strong={true}>Dashboard</Heading>
          </Box>
          <Box pad={{horizontal: 'small'}}>
            {busy ? <Spinning /> : null}
          </Box>

        </Box>
        <Box>
          <Box justify='center' direction='row'>
            {teamTile}
            {problemTile}
          </Box>
          <Box >
            <Box justify='center' direction='row'>
              {buyerTile}
              {buyerTop5Tile}
            </Box>
          </Box>
        </Box>
        {teamFilterLayer}
      </Box>
    );
  }
}

Dashboard.contextTypes = {
  router: React.PropTypes.object.isRequired
};

let select = (store) => {
  return { nav: store.nav, misc: store.misc};
};

export default connect(select)(Dashboard);
