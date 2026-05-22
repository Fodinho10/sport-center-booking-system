import { useState, useEffect, useContext } from 'react';
import axiosInstance from '../api/axiosInstance';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const MyReservations = () => {
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();

    useEffect(() => {
        if (!user) {
            navigate('/login');
            return;
        }

        const fetchMyReservations = async () => {
            try {
                const response = await axiosInstance.get('/reservations/my');
                setReservations(response.data);
                setLoading(false);
            } catch (err) {
                console.error("Hiba a foglalások lekérésekor:", err);
                setError('Nem sikerült betölteni a foglalásokat.');
                setLoading(false);
            }
        };

        fetchMyReservations();
    }, [user, navigate]);

    const handleCancel = async (reservationId) => {
        if (!window.confirm('Biztosan le szeretnéd mondani ezt a foglalást?')) return;

        try {
            const response = await axiosInstance.put(`/reservations/${reservationId}/cancel`);
            setReservations(reservations.map(res => 
                res.id === reservationId ? response.data : res
            ));
            alert('A foglalás sikeresen lemondva!');
        } catch (err) {
            console.error("Hiba a lemondáskor:", err);
            alert(err.response?.data?.message || 'Hiba történt a lemondás során!');
        }
    };

    if (loading) return <h2 style={{ textAlign: 'center', marginTop: '50px' }}>⏳ Foglalások betöltése...</h2>;
    if (error) return <h2 style={{ textAlign: 'center', color: 'red', marginTop: '50px' }}>{error}</h2>;

    return (
        <div style={{ maxWidth: '800px', margin: '0 auto' }}>
            <h2 style={{ borderBottom: '2px solid #3498db', paddingBottom: '10px' }}>📅 Saját Foglalásaim</h2>
            
            {reservations.length === 0 ? (
                <p>Még nincs egyetlen foglalásod sem.</p>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginTop: '20px' }}>
                    {reservations.map(res => (
                        <div key={res.id} style={{ 
                            border: '1px solid #ccc', 
                            borderRadius: '8px', 
                            padding: '15px', 
                            backgroundColor: res.status === 'CANCELLED' ? '#fdecea' : '#eafaf1' // Színváltás státusz alapján
                        }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <div>
                                    <h3 style={{ margin: '0 0 10px 0' }}>{res.courtName} ({res.sportType})</h3>
                                    <p style={{ margin: '5px 0' }}><strong>Időpont:</strong> {new Date(res.startTime).toLocaleString()} - {new Date(res.endTime).toLocaleString()}</p>
                                    <p style={{ margin: '5px 0' }}><strong>Végösszeg:</strong> {res.totalCost} Ft</p>
                                    <p style={{ margin: '5px 0' }}><strong>Státusz:</strong> 
                                        <span style={{ fontWeight: 'bold', color: res.status === 'CANCELLED' ? 'red' : 'green', marginLeft: '5px' }}>
                                            {res.status === 'CONFIRMED' ? 'Jóváhagyva' : 'Lemondva'}
                                        </span>
                                    </p>
                                    
                                    {res.rentedEquipment && res.rentedEquipment.length > 0 && (
                                        <div style={{ marginTop: '10px', fontSize: '0.9em', color: '#555' }}>
                                            <strong>Bérelt eszközök:</strong>
                                            <ul style={{ margin: '5px 0', paddingLeft: '20px' }}>
                                                {res.rentedEquipment.map(eq => (
                                                    <li key={eq.equipmentId}>{eq.quantity} db {eq.equipmentName}</li>
                                                ))}
                                            </ul>
                                        </div>
                                    )}
                                </div>
                                
                                {res.status !== 'CANCELLED' && (
                                    <button 
                                        onClick={() => handleCancel(res.id)}
                                        style={{ padding: '10px 15px', backgroundColor: '#e74c3c', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}
                                    >
                                        ❌ Lemondás
                                    </button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default MyReservations;