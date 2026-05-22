import { useState, useEffect, useContext } from 'react';
import axiosInstance from '../api/axiosInstance';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();

    const sportTypes = 
    ['FOOTBALL',
    'TENNIS',
    'TABLE_TENNIS',
    'HANDBALL',
    'BASKETBALL',
    'BADMINTON',
    'FUTSAL',
    'PADEL',
    'ATHLETICS'];

    const [activeTab, setActiveTab] = useState('reservations');

    const [allReservations, setAllReservations] = useState([]);
    const [courts, setCourts] = useState([]);
    const [equipments, setEquipments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [refreshCount, setRefreshCount] = useState(0);
    const [courtForm, setCourtForm] = useState({ id: null, name: '', sportType: '', hourlyRate: '', activeStatus: true });
    const [eqForm, setEqForm] = useState({ id: null, name: '', sportType: '', totalStock: '', hourlyRate: '', activeStatus: true });

    useEffect(() => {
        if (!user || user.role !== 'ROLE_ADMIN') {
            navigate('/');
            return;
        }

        const loadAllData = async () => {
            try {
                const [resResponse, courtsResponse, eqResponse] = await Promise.all([
                    axiosInstance.get('/admin/reservations'),
                    axiosInstance.get('/courts'),
                    axiosInstance.get('/equipment')
                ]);
                setAllReservations(resResponse.data);
                setCourts(courtsResponse.data);
                setEquipments(eqResponse.data);
                setLoading(false);
            } catch (err) {
                console.error("Hiba az adatok letöltésekor:", err);
                setError('Nem sikerült betölteni az admin adatokat.');
                setLoading(false);
            }
        };

        loadAllData();
    }, [user, navigate, refreshCount]);

    const handleAdminDelete = async (reservationId) => {
        if (!window.confirm('Biztosan törölni szeretnéd ezt a foglalást?')) return;
        try {
            await axiosInstance.delete(`/admin/reservations/${reservationId}`);
            setAllReservations(allReservations.filter(res => res.id !== reservationId));
            alert('Foglalás törölve!');
        // eslint-disable-next-line no-unused-vars
        } catch (err) {
            alert('Hiba a törlés során!');
        }
    };

    const handleCourtSubmit = async (e) => {
        e.preventDefault();
        const payload = {
            name: courtForm.name,
            sportType: courtForm.sportType,
            hourlyRate: parseInt(courtForm.hourlyRate)
        };

        try {
            if (courtForm.id) {
                await axiosInstance.put(`/admin/courts/${courtForm.id}`, payload);
                alert('Pálya sikeresen módosítva!');
            } else {
                await axiosInstance.post('/admin/courts', payload);
                alert('Új pálya sikeresen felvéve!');
            }
            setCourtForm({ id: null, name: '', sportType: '', hourlyRate: '', activeStatus: true });
            setRefreshCount(prev => prev + 1);
        // eslint-disable-next-line no-unused-vars
        } catch (err) {
            alert('Hiba a pálya mentésekor!');
        }
    };

    const handleEqSubmit = async (e) => {
        e.preventDefault();
        const payload = {
            name: eqForm.name,
            sportType: eqForm.sportType,
            totalInventory: parseInt(eqForm.totalStock),
            hourlyRate: parseInt(eqForm.hourlyRate),
            activeStatus: eqForm.equipmentStatus
        };

        try {
            if (eqForm.id) {
                await axiosInstance.put(`/admin/equipment/${eqForm.id}`, payload);
                alert('Eszköz sikeresen módosítva!');
            } else {
                await axiosInstance.post('/admin/equipment', payload);
                alert('Új eszköz sikeresen felvéve!');
            }
            setEqForm({ id: null, name: '', sportType: '', totalStock: '', hourlyRate: '', activeStatus: true });
            setRefreshCount(prev => prev + 1);
        // eslint-disable-next-line no-unused-vars
        } catch (err) {
            alert('Hiba az eszköz mentésekor!');
        }
    };

    if (loading) return <h2 style={{ textAlign: 'center', marginTop: '50px' }}>⏳ Admin panel betöltése...</h2>;
    if (error) return <h2 style={{ textAlign: 'center', color: 'red', marginTop: '50px' }}>{error}</h2>;

    return (
        <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
            <h2 style={{ borderBottom: '2px solid #f1c40f', paddingBottom: '10px', color: '#f39c12' }}>
                🛠️ Adminisztrátori Vezérlőpult
            </h2>

            {/* --- FÜLEK --- */}
            <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
                <button onClick={() => setActiveTab('reservations')} style={{ padding: '10px 20px', cursor: 'pointer', fontWeight: activeTab === 'reservations' ? 'bold' : 'normal', backgroundColor: activeTab === 'reservations' ? '#2c3e50' : '#ddd', color: activeTab === 'reservations' ? 'white' : 'black', border: 'none', borderRadius: '4px' }}>📅 Foglalások</button>
                <button onClick={() => setActiveTab('courts')} style={{ padding: '10px 20px', cursor: 'pointer', fontWeight: activeTab === 'courts' ? 'bold' : 'normal', backgroundColor: activeTab === 'courts' ? '#2c3e50' : '#ddd', color: activeTab === 'courts' ? 'white' : 'black', border: 'none', borderRadius: '4px' }}>🎾 Pályák Kezelése</button>
                <button onClick={() => setActiveTab('equipment')} style={{ padding: '10px 20px', cursor: 'pointer', fontWeight: activeTab === 'equipment' ? 'bold' : 'normal', backgroundColor: activeTab === 'equipment' ? '#2c3e50' : '#ddd', color: activeTab === 'equipment' ? 'white' : 'black', border: 'none', borderRadius: '4px' }}>🏸 Eszközök Kezelése</button>
            </div>

            {/* ==================== 1. FÜL: FOGLALÁSOK ==================== */}
            {activeTab === 'reservations' && (
                <div>
                    <h3>Minden foglalás a rendszerben:</h3>
                    {allReservations.length === 0 ? (
                        <p>Nincs még foglalás a rendszerben.</p>
                    ) : (
                        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                            <thead>
                                <tr style={{ backgroundColor: '#2c3e50', color: 'white' }}>
                                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>ID</th>
                                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>User Email</th>
                                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>Pálya</th>
                                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>Kezdés időpontja</th>
                                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>Befejezés időpontja</th>
                                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>Státusz</th>
                                    <th style={{ padding: '8px', border: '1px solid #ddd' }}>Művelet</th>
                                </tr>
                            </thead>
                            <tbody>
                                {allReservations.map(res => (
                                    <tr key={res.id} style={{ textAlign: 'center', backgroundColor: res.status === 'CANCELLED' ? '#fdecea' : 'white' }}>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>#{res.id}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{res.userEmail}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{res.courtName}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{new Date(res.startTime).toLocaleString()}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{new Date(res.endTime).toLocaleString()}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd', color: res.status === 'CANCELLED' ? 'red' : 'green', fontWeight: 'bold' }}>{res.status}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>
                                            <button onClick={() => handleAdminDelete(res.id)} style={{ backgroundColor: '#e74c3c', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer', borderRadius: '3px' }}>Törlés</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>
            )}

            {/* ==================== 2. FÜL: PÁLYÁK KEZELÉSE ==================== */}
            {activeTab === 'courts' && (
                <div style={{ display: 'flex', gap: '30px' }}>

                    <div style={{ flex: 1, padding: '15px', border: '1px solid #ccc', borderRadius: '5px', backgroundColor: '#f9f9f9', height: 'fit-content' }}>
                        <h3>{courtForm.id ? '✏️ Pálya Szerkesztése' : '➕ Új Pálya Felvétele'}</h3>
                        <form onSubmit={handleCourtSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                            <label>Pálya neve:</label>
                            <input type="text" value={courtForm.name} onChange={e => setCourtForm({...courtForm, name: e.target.value})} required style={{ padding: '6px' }} />
                            <label>Sport típusa:</label>
                            <select value={courtForm.sportType} onChange={e => setCourtForm({...courtForm, sportType: e.target.value})} required style={{ padding: '6px' }}>
                                <option value="">-- Válassz sportágat --</option>
                                {sportTypes.map(sport => (
                                    <option key={sport} value={sport}>{sport}</option>
                                ))}
                            </select>
                            <label>Óradíj (Ft):</label>
                            <input type="number" value={courtForm.hourlyRate} onChange={e => setCourtForm({...courtForm, hourlyRate: e.target.value})} required style={{ padding: '6px' }} />
                            <button type="submit" style={{ padding: '10px', backgroundColor: '#2ecc71', color: 'white', border: 'none', cursor: 'pointer', fontWeight: 'bold' }}>Mentés</button>
                            {courtForm.id && <button type="button" onClick={() => setCourtForm({ id: null, name: '', sportType: '', hourlyRate: '', activeStatus: true })} style={{ padding: '5px', backgroundColor: '#7f8c8d', color: 'white', border: 'none', cursor: 'pointer' }}>Mégse</button>}
                        </form>
                    </div>

                    <div style={{ flex: 2 }}>
                        <h3>Aktuális pályák listája:</h3>
                        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                            <thead>
                                <tr style={{ backgroundColor: '#34495e', color: 'white' }}><th>ID</th><th>Név</th><th>Sport</th><th>Óradíj</th><th>Művelet</th></tr>
                            </thead>
                            <tbody>
                                {courts.map(c => (
                                    <tr key={c.id} style={{ textAlign: 'center' }}>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{c.id}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{c.name}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{c.sportType}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{c.hourlyRate} Ft</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>
                                            <button onClick={() => setCourtForm({ id: c.id, name: c.name, sportType: c.sportType, hourlyRate: c.hourlyRate, activeStatus: true })} style={{ backgroundColor: '#f39c12', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer', borderRadius: '3px' }}>Szerkesztés</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* ==================== 3. FÜL: ESZKÖZÖK KEZELÉSE ==================== */}
            {activeTab === 'equipment' && (
                <div style={{ display: 'flex', gap: '30px' }}>
                    <div style={{ flex: 1, padding: '15px', border: '1px solid #ccc', borderRadius: '5px', backgroundColor: '#f9f9f9', height: 'fit-content' }}>
                        <h3>{eqForm.id ? '✏️ Eszköz Szerkesztése' : '➕ Új Eszköz Felvétele'}</h3>
                        <form onSubmit={handleEqSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                            <label>Eszköz neve:</label>
                            <input type="text" value={eqForm.name} onChange={e => setEqForm({...eqForm, name: e.target.value})} required style={{ padding: '6px' }} />
                            <label>Sport típusa:</label>
                            <select value={eqForm.sportType} onChange={e => setEqForm({...eqForm, sportType: e.target.value})} required style={{ padding: '6px' }}>
                                <option value="">-- Válassz sportágat --</option>
                                {sportTypes.map(sport => (
                                    <option key={sport} value={sport}>{sport}</option>
                                ))}
                            </select>
                            <label>Teljes készlet (db):</label>
                            <input type="number" value={eqForm.totalStock} onChange={e => setEqForm({...eqForm, totalStock: e.target.value})} required style={{ padding: '6px' }} />
                            <label>Óradíj (Ft):</label>
                            <input type="number" value={eqForm.hourlyRate} onChange={e => setEqForm({...eqForm, hourlyRate: e.target.value})} required style={{ padding: '6px' }} />
                            <button type="submit" style={{ padding: '10px', backgroundColor: '#2ecc71', color: 'white', border: 'none', cursor: 'pointer', fontWeight: 'bold' }}>Mentés</button>
                            {eqForm.id && <button type="button" onClick={() => setEqForm({ id: null, name: '', sportType: '', totalStock: '', hourlyRate: '', activeStatus: true })} style={{ padding: '5px', backgroundColor: '#7f8c8d', color: 'white', border: 'none', cursor: 'pointer' }}>Mégse</button>}
                        </form>
                    </div>

                    <div style={{ flex: 2 }}>
                        <h3>Aktuális eszközök listája:</h3>
                        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                            <thead>
                                <tr style={{ backgroundColor: '#34495e', color: 'white' }}><th>ID</th><th>Név</th><th>Sport</th><th>Készlet</th><th>Óradíj</th><th>Művelet</th></tr>
                            </thead>
                            <tbody>
                                {equipments.map(e => (
                                    <tr key={e.id} style={{ textAlign: 'center' }}>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{e.id}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{e.name}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{e.sportType}</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{e.totalInventory} db</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>{e.hourlyRate} Ft</td>
                                        <td style={{ padding: '8px', border: '1px solid #ddd' }}>
                                            <button onClick={() => setEqForm({ id: e.id, name: e.name, sportType: e.sportType, totalStock: e.totalStock, hourlyRate: e.hourlyRate, activeStatus: true })} style={{ backgroundColor: '#f39c12', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer', borderRadius: '3px' }}>Szerkesztés</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminDashboard;