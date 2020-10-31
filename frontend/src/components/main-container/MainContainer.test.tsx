import React from 'react';
import { render } from '@testing-library/react';
import { MainContainer } from './MainContainer';
import { oidcContext } from '@axa-fr/react-oidc-context/dist/oidcContext/AuthenticationContext';
import { BrowserRouter as Router } from 'react-router-dom'

describe("MainContainer", () => {
  it("Should display login link when not logged in", () => {
    const props = {
      oidcUser: null
    } as unknown as oidcContext
    const { getByText } = render(<Router><MainContainer {...props} /></Router>);
    const loginElement = getByText("Login")
    expect(loginElement).toBeInTheDocument();
  })
  it("Should display user email when logged in", () => {
    const loggedInUser = "john.smith@gmail.com"
    const props: oidcContext = {
      oidcUser: {
        profile: {
          email: loggedInUser
        }
      }
    } as unknown as oidcContext

    const { queryByText, getByText } = render(<Router><MainContainer {...props} /></Router>);
    const loginElement = queryByText("Login")
    expect(loginElement).toBeNull()
    const email = getByText(loggedInUser)
    expect(email).toBeInTheDocument();
  })
})
