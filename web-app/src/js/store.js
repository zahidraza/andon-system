import { createStore, combineReducers, applyMiddleware } from "redux";

import logger from "redux-logger";
import thunk from "redux-thunk";
import promise from "redux-promise-middleware";

import nav from "./reducers/nav";
import misc from "./reducers/misc";
import user from "./reducers/user";

const reducer = combineReducers({nav, misc, user});

const middleware = applyMiddleware(promise(), thunk, logger());

export default createStore(reducer, middleware);
