import { AuthenticationContext } from '@axa-fr/react-oidc-context'
import { oidcContext } from '@axa-fr/react-oidc-context/dist/oidcContext/AuthenticationContext'
import React, { FunctionComponent, useState } from 'react'
import { Channel } from '../../services/ChannelsService'
import { ChannelsService } from './../../services'

export const Home: FunctionComponent = () => {

    return <div>
        <AuthenticationContext.Consumer>{(props: oidcContext) =>

            <h1>Hi</h1>
        }
        </AuthenticationContext.Consumer>
    </div>

}