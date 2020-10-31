
import React, { FunctionComponent } from 'react'
import { oidcContext } from '@axa-fr/react-oidc-context/dist/oidcContext/AuthenticationContext'
import { Navbar, Image, Container, Row, Col } from 'react-bootstrap'
import { withOidcSecure } from '@axa-fr/react-oidc-context';
import { Home } from './../../routes'
import {
    Switch,
    Link,
    Route
} from "react-router-dom";

export const MainContainer: FunctionComponent<oidcContext> = (props) => <>
    <Navbar className="d-flex justify-content-between">
        <Navbar.Brand href="/">
            <Image rounded height={50} src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+CjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iMTUzOCIgaGVpZ2h0PSIyNTAwIiB2aWV3Qm94PSIwIDAgMjU2IDQxNiIgcHJlc2VydmVBc3BlY3RSYXRpbz0ieE1pbllNaW4gbWVldCI+PGRlZnM+PGxpbmVhckdyYWRpZW50IHgxPSIwJSIgeTE9IjUwJSIgeTI9IjUwJSIgaWQ9ImEiPjxzdG9wIHN0b3AtY29sb3I9IiM0RjRGNEYiIG9mZnNldD0iMCUiLz48c3RvcCBvZmZzZXQ9IjEwMCUiLz48L2xpbmVhckdyYWRpZW50PjxsaW5lYXJHcmFkaWVudCB4MT0iMCUiIHkxPSI1MCUiIHkyPSI1MCUiIGlkPSJiIj48c3RvcCBzdG9wLWNvbG9yPSIjQzQwMDAwIiBvZmZzZXQ9IjAlIi8+PHN0b3Agc3RvcC1jb2xvcj0iI0YwMCIgb2Zmc2V0PSIxMDAlIi8+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PHBhdGggZD0iTTAgMjg4di0zMmMwLTUuMzk0IDExNi4zNzctMTQuNDI4IDE5Mi4yLTMyIDM2LjYyOCA4LjQ5IDYzLjggMTguOTY5IDYzLjggMzJ2MzJjMCAxMy4wMjQtMjcuMTcyIDIzLjUxLTYzLjggMzJDMTE2LjM3NiAzMDIuNDI1IDAgMjkzLjM5IDAgMjg4IiBmaWxsPSJ1cmwoI2EpIiB0cmFuc2Zvcm09Im1hdHJpeCgxIDAgMCAtMSAwIDU0NCkiLz48cGF0aCBkPSJNMCAxNjB2LTMyYzAtNS4zOTQgMTE2LjM3Ny0xNC40MjggMTkyLjItMzIgMzYuNjI4IDguNDkgNjMuOCAxOC45NjkgNjMuOCAzMnYzMmMwIDEzLjAyNC0yNy4xNzIgMjMuNTEtNjMuOCAzMkMxMTYuMzc2IDE3NC40MjUgMCAxNjUuMzkgMCAxNjAiIGZpbGw9InVybCgjYSkiIHRyYW5zZm9ybT0ibWF0cml4KDEgMCAwIC0xIDAgMjg4KSIvPjxwYXRoIGQ9Ik0wIDIyNHYtOTZjMCA4IDI1NiAyNCAyNTYgNjR2OTZjMC00MC0yNTYtNTYtMjU2LTY0IiBmaWxsPSJ1cmwoI2IpIiB0cmFuc2Zvcm09Im1hdHJpeCgxIDAgMCAtMSAwIDQxNikiLz48cGF0aCBkPSJNMCA5NlYwYzAgOCAyNTYgMjQgMjU2IDY0djk2YzAtNDAtMjU2LTU2LTI1Ni02NCIgZmlsbD0idXJsKCNiKSIgdHJhbnNmb3JtPSJtYXRyaXgoMSAwIDAgLTEgMCAxNjApIi8+PHBhdGggZD0iTTAgMzUydi05NmMwIDggMjU2IDI0IDI1NiA2NHY5NmMwLTQwLTI1Ni01Ni0yNTYtNjQiIGZpbGw9InVybCgjYikiIHRyYW5zZm9ybT0ibWF0cml4KDEgMCAwIC0xIDAgNjcyKSIvPgoJPG1ldGFkYXRhPgoJCTxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyIgeG1sbnM6cmRmcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wMS9yZGYtc2NoZW1hIyIgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIj4KCQkJPHJkZjpEZXNjcmlwdGlvbiBhYm91dD0iaHR0cHM6Ly9pY29uc2NvdXQuY29tL2xlZ2FsI2xpY2Vuc2VzIiBkYzp0aXRsZT0ic2NhbGEtNCIgZGM6ZGVzY3JpcHRpb249InNjYWxhLTQiIGRjOnB1Ymxpc2hlcj0iSWNvbnNjb3V0IiBkYzpkYXRlPSIyMDE3LTA2LTE3IiBkYzpmb3JtYXQ9ImltYWdlL3N2Zyt4bWwiIGRjOmxhbmd1YWdlPSJlbiI+CgkJCQk8ZGM6Y3JlYXRvcj4KCQkJCQk8cmRmOkJhZz4KCQkJCQkJPHJkZjpsaT5JY29uIE1hZmlhPC9yZGY6bGk+CgkJCQkJPC9yZGY6QmFnPgoJCQkJPC9kYzpjcmVhdG9yPgoJCQk8L3JkZjpEZXNjcmlwdGlvbj4KCQk8L3JkZjpSREY+CiAgICA8L21ldGFkYXRhPjwvc3ZnPgo="></Image>
        </Navbar.Brand>
        <Navbar.Toggle />
        <Navbar.Collapse className="justify-content-end">
            <ul className="navbar-nav px-3"><li className="nav-item text-nowrap">
                {props.oidcUser ? (<p>{props.oidcUser.profile.email}</p>) : <button className="btn btn-link" onClick={e => props.login()}>Login</button>}
            </li>
            </ul>

        </Navbar.Collapse>
    </Navbar>
    <hr />
    <Container fluid>
        <div>
            <Row>
                <main role="main" className="col-md-10  offset-md-1 col-lg-8 offset-lg-2 pt-3 px-4">
                    <Row>
                        <Switch>
                            <Route component={Home} ></Route>
                        </Switch>
                    </Row>
                    <Row>
                        <Col xs={{ span: 12 }} className="p-0">
                            <div>Footer</div>
                        </Col>
                    </Row>
                </main>
            </Row>
        </div>
    </Container>
</>