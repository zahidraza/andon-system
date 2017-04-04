import { createStore, combineReducers, applyMiddleware } from "redux";

import logger from "redux-logger";
import thunk from "redux-thunk";
import promise from "redux-promise-middleware";

import nav from "./reducers/nav";

const reducer = combineReducers({
  nav
});

const middleware = applyMiddleware(promise(), thunk, logger());

export default createStore(reducer, middleware);
