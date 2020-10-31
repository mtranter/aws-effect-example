import { UserManagerSettings } from "oidc-client"

const OidcConfiguration: UserManagerSettings = {
    client_id: process.env["REACT_APP_OAUTH_CLIENT_ID"]!, // oidc client configuration, the same as oidc client library used internally https://github.com/IdentityModel/oidc-client-js
    redirect_uri: `${window.location.protocol}//${window.location.host}/auth/callback`,
    response_type: "code",
    scope: 'openid profile email',
    authority: process.env["REACT_APP_OAUTH_URL"]!,
    silent_redirect_uri: `${window.location.protocol}//${window.location.host}/auth/callback/silent`,
    post_logout_redirect_uri: '/', // optional
    metadata: {
        issuer: process.env["REACT_APP_OAUTH_ISSUER"]!,
        jwks_uri: `${process.env["REACT_APP_OAUTH_URL"]}/.well-known/jwks.json`,
        authorization_endpoint: `${process.env["REACT_APP_OAUTH_URL"]}/authorize`,
        token_endpoint: `${process.env["REACT_APP_OAUTH_URL"]}/oauth/token`,
        userinfo_endpoint: `${process.env["REACT_APP_OAUTH_URL"]}/userInfo`,
        end_session_endpoint: `${process.env["REACT_APP_OAUTH_URL"]}/logout`,
    }   
}

export default OidcConfiguration


