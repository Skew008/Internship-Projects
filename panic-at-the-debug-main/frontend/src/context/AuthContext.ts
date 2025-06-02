// import React, { createContext, useState, useEffect, useContext, ReactNode } from 'react';

// // Define types
// interface User {
//   username: string;
//   role: string;
// }

// interface AuthContextProps {
//   user: User | null;
//   login: (userData: User, token: string) => void;
//   logout: () => void;
// }

// // Create context with initial values
// const AuthContext = createContext<AuthContextProps>({
//   user: null,
//   login: () => {},
//   logout: () => {}
// });

// // Provider props interface
// interface AuthProviderProps {
//   children: ReactNode;
// }

// // Auth provider component
// export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
//   const [user, setUser] = useState<User | null>(null);
  
//   useEffect(() => {
//     const token = localStorage.getItem('accessToken');
//     const userData = localStorage.getItem('user');
    
//     if (token && userData) {
//       try {
//         setUser(JSON.parse(userData));
//       } catch (e) {
//         localStorage.removeItem('accessToken');
//         localStorage.removeItem('user');
//       }
//     }
//   }, []);
  
//   const login = (userData: User, token: string) => {
//     localStorage.setItem('accessToken', token);
//     localStorage.setItem('user', JSON.stringify(userData));
//     setUser(userData);
//   };
  
//   const logout = () => {
//     localStorage.removeItem('accessToken');
//     localStorage.removeItem('user');
//     setUser(null);
//   };
  
//   return (
//     <AuthContext.Provider value={{ user, login, logout }}>
//       {children}
//     </AuthContext.Provider>
//   );
// };

// // Custom hook
// export const useAuth = (): AuthContextProps => {
//   return useContext(AuthContext);
// };