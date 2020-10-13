import { Auth0ContextInterface } from '@auth0/auth0-react';

export interface Channel {
    id: string,
    creator: string,
    description: string
}

export const ChannelsService = {
    getChannels: (authToken: string) => {
        return fetch("https://k112rvlek9.execute-api.ap-southeast-2.amazonaws.com/dev/channels", {
            headers: {
                Authorization: `Bearer ${authToken}`
            }
        }).then(r => r.json()).then(r => r as Channel[])
    }
}