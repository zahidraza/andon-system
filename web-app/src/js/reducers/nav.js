import { NAV_ACTIVATE } from "../actions";

const initialState = {
  active: false,
  items:[
    { path: '/dashboard2', label: 'Home'},
    { path: '/report2', label: 'Report'},
    { path: '/user', label: 'User'},
    { path: '/mapping2', label: 'Mapping'},
    { path: '/tracking', label: 'Issue Tracking'},
    { path: '/problem', label: 'Problem'},
    { path: '/download', label: 'Download'}
  ],
  itemsFactory: [
    { path: '/dashboard1', label: 'Home'},
    { path: '/report1', label: 'Report'},
    { path: '/user', label: 'User'},
    { path: '/mapping1', label: 'Mapping'},
    { path: '/section', label: 'Section'},
    { path: '/department', label: 'Department'},
    { path: '/download', label: 'Download'}
  ]
};

export default function nav ( state = initialState, action) {

  switch ( action.type) {
    case NAV_ACTIVATE : {
      state = {...state, active: action.payload.active};
      break;
    }
  }
  return state;
}
