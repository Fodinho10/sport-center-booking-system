import { Routes, Route, Link, useNavigate } from 'react-router-dom';
import { useContext } from 'react';
import { AuthContext } from './context/AuthContext';

import Register from './pages/Register';
import CreateReservation from './pages/CreateReservation';
import MyReservations from './pages/MyReservations';
import Home from './pages/Home';
import Login from './pages/Login';
import AdminDashboard from './pages/AdminDashboard'; 

function App() {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/'); 
  };

  return (
    <div>
      <nav style={{ padding: '15px', background: '#2c3e50', color: 'white', display: 'flex', gap: '20px', alignItems: 'center', borderRadius: '8px'}}>
        <h3 style={{ margin: 0 }}>🏆 Sportcentrum</h3>
        <Link to="/" style={{ color: 'white', textDecoration: 'none' }}>Kezdőlap</Link>
        
        {!user ? (
          <Link to="/login" style={{ color: 'white', textDecoration: 'none', marginLeft: 'auto' }}>Belépés</Link>
        ) : (
          <div style={{ marginLeft: 'auto', display: 'flex', gap: '15px', alignItems: 'center' }}>
            
            {user.role === 'ROLE_ADMIN' && (
              <Link to="/admin" style={{ color: '#f1c40f', textDecoration: 'none', fontWeight: 'bold' }}>Admin Panel</Link>
            )}

            <Link to="/my-reservations" style={{ color: '#ecf0f1', textDecoration: 'none', fontWeight: 'bold' }}>Foglalásaim</Link>
            <Link to="/new-reservation" style={{ color: '#2ecc71', textDecoration: 'none', fontWeight: 'bold' }}>+ Új Foglalás</Link>
            
            <span style={{ color: '#bdc3c7' }}>Szia, {user.email}!</span>
            <button onClick={handleLogout} style={{ cursor: 'pointer', padding: '5px 10px' }}>Kijelentkezés</button>
          </div>
        )}
      </nav>

      <div style={{ padding: '20px' }}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/my-reservations" element={<MyReservations />} />
          <Route path="/new-reservation" element={<CreateReservation />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;
    



