import {MISC_CONSTANTS as c} from "../utils/constants";

const initialState = {
  initialized: false,
  teams: [],
  problems: [], //v2 problems. fixed four problems
  buyers: [],

  sections: [],
  departments: [],
  problms: [],  //v1 problems. contains entire mapping details
  desgns: []

};

const handlers = { 
  [c.INITIALIZE_TEAM]: (_, action) => ({teams: action.payload.teams}),
  [c.INITIALIZE_PROBLEM]: (_, action) => ({problems: action.payload.problems}),
  [c.INITIALIZE_BUYER]: (_, action) => ({buyers: action.payload.buyers}),
  [c.INITIALIZE_SECTION]: (_, action) => ({sections: action.payload.sections}),
  [c.INITIALIZE_DEPARTMENT]: (_, action) => ({departments: action.payload.departments}),
  [c.INITIALIZE_PROBLM]: (_, action) => ({problms: action.payload.problms}),
  [c.INITIALIZE_DESGN]: (_, action) => ({desgns: action.payload.desgns}),
  [c.STORE_INITIALIZED]: (_, action) => ({initialized: true})
};

export default function section (state = initialState, action) {
  let handler = handlers[action.type];
  if( !handler ) return state;
  return { ...state, ...handler(state, action) };
}
