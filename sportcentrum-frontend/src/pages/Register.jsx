import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';

const Register = () => {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: ''
    });
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');

        try {
            await axiosInstance.post('/auth/register', formData);
            
            setSuccessMessage('Sikeres regisztráció! Most már bejelentkezhetsz.');
            
            setTimeout(() => {
                navigate('/login');
            }, 2000);
            
        } catch (err) {
            console.error('Hiba a regisztrációkor:', err);
            const backendMessage = err.response?.data?.message || err.response?.data?.error || 'Hiba történt a regisztráció során! (Lehet, hogy ez az e-mail már foglalt?)';
            setError(backendMessage);
        }
    };

    return (
        <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px', boxShadow: '0 4px 8px rgba(0,0,0,0.1)', backgroundColor: '#fff' }}>
            <h2 style={{ textAlign: 'center', marginBottom: '20px', color: '#2c3e50' }}>Regisztráció</h2>
            
            {error && <div style={{ color: 'red', marginBottom: '15px', textAlign: 'center', fontWeight: 'bold' }}>{error}</div>}
            {successMessage && <div style={{ color: 'green', marginBottom: '15px', textAlign: 'center', fontWeight: 'bold' }}>{successMessage}</div>}
            
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <div style={{ display: 'flex', gap: '10px' }}>
                    <div style={{ flex: 1 }}>
                        <label style={{ display: 'block', marginBottom: '5px' }}>Név:</label>
                        <input type="text" name="name" value={formData.name} onChange={handleChange} required style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc', boxSizing: 'border-box' }} />
                    </div>
                </div>
                
                <div>
                    <label style={{ display: 'block', marginBottom: '5px' }}>E-mail cím:</label>
                    <input type="email" name="email" value={formData.email} onChange={handleChange} required style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc', boxSizing: 'border-box' }} />
                </div>
                
                <div>
                    <label style={{ display: 'block', marginBottom: '5px' }}>Jelszó:</label>
                    <input type="password" name="password" value={formData.password} onChange={handleChange} required style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc', boxSizing: 'border-box' }} />
                </div>
                
                <button type="submit" style={{ padding: '10px', background: '#3498db', color: 'white', border: 'none', borderRadius: '4px', fontSize: '16px', cursor: 'pointer', marginTop: '10px', fontWeight: 'bold' }}>
                    Regisztrálok
                </button>
            </form>

            <div style={{ marginTop: '15px', textAlign: 'center', fontSize: '14px' }}>
                Már van fiókod? <Link to="/login" style={{ color: '#3498db', fontWeight: 'bold' }}>Jelentkezz be itt!</Link>
            </div>
        </div>
    );
};

export default Register;