import { NAV_ACTIVATE } from "../actions";

const initialState = {
  active: false,
  items:[
    { path: '/dashboard2', label: 'Home'},
    { path: '/report2', label: 'Report'},
    { path: '/user2', label: 'User'},
    { path: '/buyer', label: 'Mapping'},
    { path: '/problem2', label: 'Problem'}
  ],
  itemsFactory: [
    { path: '/dashboard1', label: 'Home'},
    { path: '/section', label: 'Section'},
    { path: '/department', label: 'Department'},
    { path: '/problem1', label: 'Problem'},
    { path: '/user1', label: 'User'},
    { path: '/test', label: 'Test Page'}
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
