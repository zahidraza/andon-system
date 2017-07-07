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
      line: {
        title: 'Line Wise',
        loaded: false,
        elements: {},
        type: 'minute',
        weeks: 1
      },
      lineDept: {
        title: 'Department Wise (Within Line)',
        loaded: false,
        elements: {},
        type: 'minute',
        weeks: 1
      },
      department: {
        title: 'Department Wise',
        loaded: false,
        elements: {},
        type: 'minute',
        weeks: 1
      },
      deptProblem: {
        title: 'Problem Wise (Within Department)',
        loaded: false,
        elements: {},
        type: 'minute',
        weeks: 1
      },
      lineFilter: {
        title: 'Select Line',
        show: false,
        items: ['Line 1','Line 2','Line 3','Line 4','Line 5','Line 6','Line 7','Line 8'],
        selected: 'Line 1'
      },
      deptFilter: {
        title: 'Select Department',
        show: false,
        items: [],
        selected: ''
      }
    };
    this.localeData = localeData();
    this._renderTile = this._renderTile.bind(this);
    this._getDowntimeLineWise = this._getDowntimeLineWise.bind(this);
    this._getDowntimeDepartmentWiseWithinLine = this._getDowntimeDepartmentWiseWithinLine.bind(this);
    this._getDowntimeDepartmentwise = this._getDowntimeDepartmentwise.bind(this);
    this._getDowntimeProblemWiseWithinDepartment = this._getDowntimeProblemWiseWithinDepartment.bind(this);
    this._renderFilterLayer = this._renderFilterLayer.bind(this);
  }

  componentWillMount () {
    console.log('componentWillMount');
    if (!this.props.misc.initialized) {
      this.setState({initializing: true});
      this.props.dispatch(initialize());
    }
    const {line,lineDept,department,deptProblem} = this.state;
    this._getDowntimeLineWise(line.weeks);
    this._getDowntimeDepartmentWiseWithinLine(lineDept.weeks);
    this._getDowntimeDepartmentwise(department.weeks);
    this._getDowntimeProblemWiseWithinDepartment(deptProblem.weeks);
  }

  componentWillReceiveProps (nextProps) {
    if (!this.props.misc.initialized && nextProps.misc.initialized) {
      this.setState({initializing: false});
    }
  }

  _getDowntimeLineWise (weeks) {
    let {line, lineDept, department, deptProblem} = this.state;
    let after = new Date().getTime();
    after = after - (weeks*7*24*60*60*1000);
    axios.get(window.serviceHost + '/v1/issues/downtime/byLine?after=' + after, {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        line.elements = response.data;
        line.loaded = true;
        let busy = true;
        if (line.loaded && lineDept.loaded && department.loaded && deptProblem.loaded) busy = false;
        this.setState({line, busy});
      }
    }).catch( (err) => {
      console.log(err);
    });
  }

  _getDowntimeDepartmentWiseWithinLine (weeks) {
    let {line, lineDept, department, deptProblem} = this.state;
    let after = new Date().getTime();
    after = after - (weeks*7*24*60*60*1000);
    axios.get(window.serviceHost + '/v1/issues/downtime/byLine?expand=true&after=' + after, {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        lineDept.elements = response.data;
        lineDept.loaded = true;
        let busy = true;
        if (line.loaded && lineDept.loaded && department.loaded && deptProblem.loaded) busy = false;
        this.setState({lineDept, busy});
      }
    }).catch( (err) => {
      console.log(err);
    });
  }

  _getDowntimeDepartmentwise (weeks) {
    let {line, lineDept, department, deptProblem} = this.state;
    let after = new Date().getTime();
    after = after - (weeks*7*24*60*60*1000);
    axios.get(window.serviceHost + '/v1/issues/downtime/byDepartment?after=' + after, {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        department.elements = response.data;
        department.loaded = true;
        let busy = true;
        if (line.loaded && lineDept.loaded && department.loaded && deptProblem.loaded) busy = false;
        this.setState({department, busy});
      }
    }).catch( (err) => {
      console.log(err);
    });
  }

  _getDowntimeProblemWiseWithinDepartment (weeks) {
    let {line, lineDept, department, deptProblem, deptFilter} = this.state;
    let after = new Date().getTime();
    after = after - (weeks*7*24*60*60*1000);
    axios.get(window.serviceHost + '/v1/issues/downtime/byDepartment?expand=true&after=' + after, {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        deptProblem.elements = response.data;
        deptProblem.loaded = true;
        const departments = Object.keys(deptProblem.elements);
        deptFilter.selected = departments[0];
        deptFilter.items = departments;
        let busy = true;
        if (line.loaded && lineDept.loaded && department.loaded && deptProblem.loaded) busy = false;
        this.setState({deptProblem,deptFilter, busy});
      }
    }).catch( (err) => {
      console.log(err);
    });
  }

  _onTypeChange (type, what) {
    let {line,lineDept,department,deptProblem} = this.state;
    if (what == 'line') {
      line.type = type;
    }else if(what == 'lineDept') {
      lineDept.type = type;
    }else if(what == 'department') {
      department.type = type;
    } else if (what == 'deptProblem') {
      deptProblem.type = type;
    }
    this.setState({line,lineDept,department,deptProblem});
  }

  _onWeekChange (weeks, what) {
    let {line,lineDept,department,deptProblem} = this.state;
    if (what == 'line') {
      line.weeks = weeks;
      this._getDowntimeLineWise(weeks);
    }else if(what == 'lineDept') {
      lineDept.weeks = weeks;
      this._getDowntimeDepartmentWiseWithinLine(weeks);
    }else if(what == 'department') {
      department.weeks = weeks;
      this._getDowntimeDepartmentwise(weeks);
    }else if(what == 'deptProblem') {
      deptProblem.weeks = weeks;
      this._getDowntimeProblemWiseWithinDepartment(weeks);
    }
    this.setState({line,lineDept, department, deptProblem, busy: true});
  }

  _onClose (which) {
    let {deptFilter, lineFilter} = this.state;
    if (which == 'department') {
      deptFilter.show = false;
    } else if (which == 'line') {
      lineFilter.show = false;
    }
    this.setState({deptFilter, lineFilter});
  }

  _onFilterActive (which) {
    let {deptFilter, lineFilter} = this.state;
    if (which == 'department') {
      deptFilter.show = true;
    } else if (which == 'line') {
      lineFilter.show = true;
    }
    this.setState({deptFilter, lineFilter});
  }

  _onFilter (which, event) {
    let {deptFilter, lineFilter} = this.state; 
    let value = event.value;
    if (which == 'department') {
      deptFilter.selected = value;
    } else if (which == 'line') {
      lineFilter.selected = value;
    }
    this.setState({deptFilter, lineFilter});
  }

  _renderFilterLayer (which) {
    let {deptFilter, lineFilter} = this.state;
    let filter;
    if (which == 'department') {
      filter = deptFilter;
    }else if (which == 'line') {
      filter = lineFilter;
    } 
    return (
      <Layer hidden={!filter.show} closer={true} onClose={this._onClose.bind(this, which)}>
        <Box margin='medium'>
          <Box><h3>{filter.title}</h3></Box>
          <Box margin='small'>
            <Select options={filter.items}
            value={filter.selected}
            onChange={this._onFilter.bind(this, which)}/>
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
   * what: Tile for what = linewise | lineDeptwise | departmentwise
   * max: sum total of values
   */
  _renderTile (title, type, weeks, elements, what, max, filter) {
    if (type == 'percent') {
      elements = elements.map(e => {
        return {...e, value: +((e.value/max)*100).toFixed(2)};
      });
      max = 100;
    }
    let which;
    if (filter != null) {
      if (filter.title.includes('Line')) {
        which = 'line';
      }else if (filter.title.includes('Department')) {
        which = 'department';
      }
    }


    if (true) {
      return (
        <Box flex={true} direction='column' full='horizontal' separator='all' pad={{vertical: 'small', horizontal: 'small'}} margin='small'>
          <Box direction='column'>
            <Box pad={{vertical: 'small'}} direction='row' justify='between'>
              <Box direction='row'>
                <Box pad={{horizontal: 'small'}}>{title}</Box>
                {filter != null ? <Box pad={{horizontal: 'small'}}>{filter.selected}</Box>: null}
                {filter != null ? <Box pad={{horizontal: 'small'}}><FilterIcon onClick={this._onFilterActive.bind(this, which)}/></Box>: null}
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
    const {initializing, busy, line, lineDept, department, deptProblem, deptFilter, lineFilter} = this.state;
    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }
    let lines = [];
    let max1 = 0;
    Object.keys(line.elements).forEach((key, i)=> {
      let minute = Math.round(line.elements[key]);
      max1 += minute;
      let color = (i < 4) ? 'neutral-' + (i+1) :( (i < 6) ?  'accent-' + (i-4+1) : 'grey-' + (i-6+1));
      lines.push({"label": key, "value": minute, "colorIndex": color});
    });
    lines.sort((a,b) => a.label < b.label ? -1 : 1);

    let lineDepts = [];
    let deptLine = lineDept.elements[lineFilter.selected]; //Department of Line X
    let max2 = 0;
    if (deptLine != undefined ) {
      Object.keys(deptLine).forEach((key,i) => {
        let minute = Math.round(deptLine[key]);
        max2 += minute;
        let color = (i < 4) ? 'neutral-' + (i+1) :( (i < 6) ?  'accent-' + (i-4+1) : 'grey-' + (i-6+1));
        lineDepts.push({"label": key, "value": minute, "colorIndex": color});
      });
    }
    console.log(lineDepts);
    console.log(max2);

    let departments = [];
    let max3 = 0;
    Object.keys(department.elements).forEach((key, i)=> {
      let minute = Math.round(department.elements[key]);
      max3 += minute;
      let color = (i < 4) ? 'neutral-' + (i+1) :( (i < 6) ?  'accent-' + (i-4+1) : 'grey-' + (i-6+1));
      departments.push({"label": key, "value": minute, "colorIndex": color});
    });

    let deptProblems = [];
    let problemDept = deptProblem.elements[deptFilter.selected]; //Problem of Department X
    let max4 = 0;
    if (problemDept != undefined ) {
      Object.keys(problemDept).forEach((key,i) => {
        let minute = Math.round(problemDept[key]);
        max4 += minute;
        let color = (i < 4) ? 'neutral-' + (i+1) :( (i < 6) ?  'accent-' + (i-4+1) : 'grey-' + (i-6+1));
        deptProblems.push({"label": key, "value": minute, "colorIndex": color});
      });
    }


    let lineTile = this._renderTile(line.title, line.type, line.weeks, lines,'line', max1, null);
    let lineDeptTile = this._renderTile(lineDept.title, lineDept.type, lineDept.weeks, lineDepts, 'lineDept', max2, lineFilter);
    let departmentTile = this._renderTile(department.title, department.type, department.weeks, departments, 'department', max3, null);
    let DeptProblemTile = this._renderTile(deptProblem.title, deptProblem.type, deptProblem.weeks, deptProblems, 'deptProblem', max4,deptFilter);
    
    const LineFilterLayer = this._renderFilterLayer('line');
    const DeptFilterLayer = this._renderFilterLayer('department');
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
            {lineTile}
            {lineDeptTile}
          </Box>
          <Box >
            <Box justify='center' direction='row'>
              {departmentTile}
              {DeptProblemTile}
            </Box>
          </Box>
        </Box>
        {LineFilterLayer}
        {DeptFilterLayer}
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
