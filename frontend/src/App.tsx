import React, { useEffect, useState } from 'react';
import './App.css';
import LoginButton from "./components/LoginButton";
import { useAuthToken } from './hooks/useAuthToken'
import { ChannelsService, Channel } from './services/ChannelsService'

function App() {
  const [channels, setChannels] = useState<Channel[]>()
  const authToken = useAuthToken("https://serverless-scala.com", "read:all")

  useEffect(() => {
    if (authToken) {
      ChannelsService.getChannels(authToken).then(setChannels)
    }
  }, [authToken])

  return (
    <div className="App">

      <LoginButton />
    </div>
  );
}

export default App;
