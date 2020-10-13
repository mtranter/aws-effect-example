import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import { Auth0Provider } from '@auth0/auth0-react';
import * as serviceWorker from './serviceWorker';

ReactDOM.render(
  <React.StrictMode>
      <Auth0Provider
    domain="sevilla.au.auth0.com"
    clientId="OTRPv8mErB3lmux35OgHER6bMCpwJKKz"
    redirectUri={window.location.origin}
    audience="https://serverless-scala.com"
    scope="read:all"
  >
    <App />
  </Auth0Provider>
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();