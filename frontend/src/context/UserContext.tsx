import { useRouter } from 'next/navigation';
import { createContext, useContext, useEffect, useState } from 'react';

interface User {
  userId: string;
  email: string;
  name: string | null;
  profileImage: string | null;
}

// 사용자 정보를 제공하기 위한 Context 생성 (상태관리 라이브러리 사용 권장)
// 지금은 간단히 React Context 사용
interface UserContextType {
  user: User | null;
  isLoading: boolean;
  error: Error | null;
}
const UserContext = createContext<UserContextType | undefined>(undefined);

export function UserProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const router = useRouter();

  useEffect(() => {
    const fetchUser = async () => {
      setIsLoading(true);
      setError(null);
      try {
        const response = await fetch('http://localhost:8080/api/users/me');

        if (!response.ok) {
          if (response.status === 401 || response.status === 403) {
            console.error('Authentication error, redirecting to login...');
            //throw new Error('Unauthorized')
            router.push('/login');
          }
          throw new Error(`Failed to fetch user: ${response.statusText}`);
        }

        const userData: User = await response.json();
        setUser(userData);
      } catch (e) {
        console.error('Error fetching user data:', e);
        setError(e instanceof Error ? e : new Error('Unknown error occurred'));
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUser();
  }, [router]);

  const contextValue = { user, isLoading, error };
  return <UserContext.Provider value={contextValue}>{children}</UserContext.Provider>;
}

// 사용자 정보를 사용하기 위한 커스텀 훅
export function useUser() {
  const context = useContext(UserContext);
  if (context === undefined) {
    throw new Error('useUser must be used within a UserProvider');
  }
  return context;
}
