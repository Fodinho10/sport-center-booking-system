import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';
import { AuthContext } from '../context/AuthContext';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(''); 

        try {
            const response = await axiosInstance.post('/auth/login', {
                email: email,
                password: password
            });

            const token = response.data.token || response.data;

            login(token);

            navigate('/');
            
        } catch (err) {
            console.error('Hiba a bejelentkezéskor:', err);
            if (err.response) {
                setError('Hibás e-mail cím vagy jelszó!');
            } else {
                setError('Hálózati hiba! Nem érhető el a szerver.');
            }
        }
    };

    return (
        <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px', boxShadow: '0 4px 8px rgba(0,0,0,0.1)' }}>
            <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>Bejelentkezés</h2>
            
            {error && <div style={{ color: 'red', marginBottom: '15px', textAlign: 'center', fontWeight: 'bold' }}>{error}</div>}
            
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <div>
                    <label style={{ display: 'block', marginBottom: '5px' }}>E-mail cím:</label>
                    <input 
                        type="email" 
                        value={email} 
                        onChange={(e) => setEmail(e.target.value)} 
                        required 
                        style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc', boxSizing: 'border-box' }}
                    />
                </div>
                <div>
                    <label style={{ display: 'block', marginBottom: '5px' }}>Jelszó:</label>
                    <input 
                        type="password" 
                        value={password} 
                        onChange={(e) => setPassword(e.target.value)} 
                        required 
                        style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc', boxSizing: 'border-box' }}
                    />
                </div>
                <button 
                    type="submit" 
                    style={{ padding: '10px', background: '#3498db', color: 'white', border: 'none', borderRadius: '4px', fontSize: '16px', cursor: 'pointer', marginTop: '10px' }}
                >
                    Belépés
                </button>
            </form>
            <div style={{ marginTop: '15px', textAlign: 'center', fontSize: '14px' }}>
                Nincs még fiókod? <a href="/register" style={{ color: '#3498db', fontWeight: 'bold', textDecoration: 'none' }}>Regisztrálj itt!</a>
            </div>
        </div>
    );
};

export default Login;