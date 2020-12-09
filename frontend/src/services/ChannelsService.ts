
export interface Channel {
    id: string,
    creator: string,
    description: string
}

export const ChannelsService = {
    getChannels: (authToken: string) => {
        return fetch("https://tpok76uzv3.execute-api.ap-southeast-1.amazonaws.com/v1/channels", {
            headers: {
                Authorization: `Bearer ${authToken}`
            }
        }).then(r => r.json()).then(r => r as Channel[])
    }
}