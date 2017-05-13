import {MISC_CONSTANTS as c} from "../utils/constants";

const initialState = {
  initialized: false,
  teams: [],
  problems: [],
  buyers: []
};

const handlers = { 
  [c.INITIALIZE_TEAM]: (_, action) => ({teams: action.payload.teams}),
  [c.INITIALIZE_PROBLEM]: (_, action) => ({problems: action.payload.problems}),
  [c.INITIALIZE_BUYER]: (_, action) => ({buyers: action.payload.buyers}),
  [c.STORE_INITIALIZED]: (_, action) => ({initialized: true})
};

export default function section (state = initialState, action) {
  let handler = handlers[action.type];
  if( !handler ) return state;
  return { ...state, ...handler(state, action) };
}
