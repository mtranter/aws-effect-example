import React, { FunctionComponent } from 'react';
import './App.scss';
import OidcConfig from './oidc-configuration'
import {
  BrowserRouter as Router
} from "react-router-dom";
import { AuthenticationProvider, oidcLog, AuthenticationContext } from '@axa-fr/react-oidc-context';
import { MainContainer } from './components'
import { oidcContext } from '@axa-fr/react-oidc-context/dist/oidcContext/AuthenticationContext';

const CallbackElement = () => <p>... validating login</p>

export const App: FunctionComponent = () =>
  <Router>
    <AuthenticationProvider configuration={OidcConfig} loggerLevel={oidcLog.DEBUG} callbackComponentOverride={CallbackElement} >
      <AuthenticationContext.Consumer>{(props: oidcContext) => <MainContainer {...props} ></MainContainer>}
      </AuthenticationContext.Consumer>
    </AuthenticationProvider>
  </Router>
