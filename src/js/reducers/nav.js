import { NAV_ACTIVATE } from "../actions";

const initialState = {
  active: false,
  items:[
    { path: '/', label: 'Home'},
    { path: '/test', label: 'Test Page'}
  ]
};

export default function nav ( state = initialState, action) {

  switch ( action.type) {
    case NAV_ACTIVATE : {
      state = {...state, active: action.active};
      break;
    }
  }
  return state;
}
