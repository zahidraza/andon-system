import { NAV_ACTIVATE } from "../actions";

const initialState = {
  active: false,
  items:[
    { path: '/dashboard2', label: 'Home'},
    { path: '/user2', label: 'User'},
    { path: '/team', label: 'Team'},
    { path: '/buyer', label: 'Buyer'},
    { path: '/problem2', label: 'Problem'},
    { path: '/test', label: 'Test Page'}
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
