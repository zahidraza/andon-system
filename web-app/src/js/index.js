import '../scss/index.scss';

import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { Router, hashHistory} from "react-router";

import routes from "./routes";
import store from "./store";

(function () {
  const baseUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '');
  window.serviceHost = baseUrl + "/api";
  window.baseUrl = baseUrl;

  // window.baseUrl = "http://andonsystem.in/andon-system";
  // window.serviceHost = "http://andonsystem.in/andon-system/api";
  
  // window.baseUrl = "http://localhost:8001";
  // window.serviceHost = "http://localhost:8001/api";
})();

let element = document.getElementById('content');
ReactDOM.render((
  <div>
    <Provider store={store} >
        <Router routes={routes} history={hashHistory} />
    </Provider>
  </div>
), element);

document.body.classList.remove('loading');

http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.rpm

http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.rpm
