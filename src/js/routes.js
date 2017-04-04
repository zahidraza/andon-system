import Dashboard from "./components/Dashboard";
import Main from "./components/Main";
import Test from "./components/Test";

export default {
  path: '/',
  component: Main,
  indexRoute: {component: Dashboard},
  childRoutes: [
    { path: 'test', component: Test}
  ]
};
