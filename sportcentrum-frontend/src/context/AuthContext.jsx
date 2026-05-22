import { createContext, useState } from 'react';
import { jwtDecode } from 'jwt-decode';

// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    
    const [user, setUser] = useState(() => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const decoded = jwtDecode(token);
                return { email: decoded.sub, id: decoded.id, role: decoded.role };
            } catch (error) {
                console.error('Érvénytelen token', error);
                localStorage.removeItem('token');
                return null;
            }
        }
        return null;
    });

    const login = (token) => {
        localStorage.setItem('token', token);
        const decoded = jwtDecode(token);
        setUser({ email: decoded.sub, id: decoded.id, role: decoded.role });
    };

    const logout = () => {
        localStorage.removeItem('token');
        setUser(null);
    };

    return (
        // Kivettük a 'loading'-ot innen is
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};