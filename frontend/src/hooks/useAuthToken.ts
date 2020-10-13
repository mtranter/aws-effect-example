import { useEffect, useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';

export const useAuthToken = (audience: string, scope: string) => {
  const { getAccessTokenSilently } = useAuth0();
  const [token, setToken] = useState<string>();

  useEffect(() => {
    getAccessTokenSilently({
          audience,
          scope
        }).then(t =>
             setToken(t)
             )
  }, [getAccessTokenSilently, token, audience, scope]);

  return token
};