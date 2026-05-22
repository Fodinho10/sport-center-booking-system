import { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';

const Home = () => {
    const [courts, setCourts] = useState([]);
    const [equipments, setEquipments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [courtsResponse, equipmentResponse] = await Promise.all([
                    axiosInstance.get('/courts'),
                    axiosInstance.get('/equipment')
                ]);

                setCourts(courtsResponse.data);
                setEquipments(equipmentResponse.data);
                setLoading(false);
            } catch (err) {
                console.error("Hiba az adatok letöltésekor:", err);
                setError('Nem sikerült betölteni az adatokat. Ellenőrizd, hogy fut-e a szerver!');
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    if (loading) return <h2 style={{ textAlign: 'center', marginTop: '50px' }}>⏳ Adatok betöltése...</h2>;
    if (error) return <h2 style={{ textAlign: 'center', color: 'red', marginTop: '50px' }}>{error}</h2>;

    return (
        <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
            <h1 style={{ textAlign: 'center', borderBottom: '2px solid #3498db', paddingBottom: '10px' }}>
                Üdvözlünk a Sportcentrumban! 🏆
            </h1>

            {/* --- PÁLYÁK SZEKCIÓ --- */}
            <h2 style={{ marginTop: '30px' }}>⛳ Foglalható Pályáink</h2>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '20px' }}>
                {courts.map(court => (
                    <div key={court.id} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '15px', backgroundColor: '#f9f9f9', boxShadow: '2px 2px 8px rgba(0,0,0,0.05)' }}>
                        <h3 style={{ margin: '0 0 10px 0', color: '#2c3e50' }}>{court.name} </h3>
                        <p style={{ margin: '5px 0' }}><strong>Sport:</strong> {court.sportType}</p>
                        <p style={{ margin: '5px 0' }}><strong>Óradíj:</strong> {court.hourlyRate} Ft / óra</p>
                    </div>
                ))}
            </div>

            {/* --- ESZKÖZÖK SZEKCIÓ --- */}
            <h2 style={{ marginTop: '40px' }}>🏸 Bérelhető Eszközeink</h2>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '20px' }}>
                {equipments.map(eq => (
                    <div key={eq.id} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '15px', backgroundColor: '#f9f9f9', boxShadow: '2px 2px 8px rgba(0,0,0,0.05)' }}>
                        <h3 style={{ margin: '0 0 10px 0', color: '#2c3e50' }}>{eq.name}</h3>
                        <p style={{ margin: '5px 0' }}><strong>Sport:</strong> {eq.sportType}</p>
                        <p style={{ margin: '5px 0' }}><strong>Készlet:</strong> {eq.totalStock} db</p>
                        <p style={{ margin: '5px 0' }}><strong>Bérleti díj:</strong> {eq.hourlyRate} Ft / óra</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Home;