import { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';
import { AuthContext } from '../context/AuthContext';

const CreateReservation = () => {
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();

    const [courtId, setCourtId] = useState('');
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const [equipmentItems, setEquipmentItems] = useState([]);

    const [courts, setCourts] = useState([]);
    const [equipments, setEquipments] = useState([]);
    const [bookedSlots, setBookedSlots] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!user) {
            navigate('/login');
            return;
        }
        const fetchOptions = async () => {
            try {
                const [courtsRes, eqRes] = await Promise.all([
                    axiosInstance.get('/courts'),
                    axiosInstance.get('/equipment')
                ]);
                setCourts(courtsRes.data);
                setEquipments(eqRes.data);
            } catch (err) {
                console.error("Hiba az opciók betöltésekor:", err);
                setError('Nem sikerült betölteni a pályákat és eszközöket.');
            }
        };
        fetchOptions();
    }, [user, navigate]);

    useEffect(() => {
        if (!courtId) {
            // eslint-disable-next-line react-hooks/set-state-in-effect
            setBookedSlots([]);
            return;
        }
        const fetchBookedSlots = async () => {
            try {
                const res = await axiosInstance.get(`/reservations/court/${courtId}`);
                setBookedSlots(res.data);
            } catch (err) {
                console.error("Nem sikerült letölteni a foglalt idősávokat:", err);
            }
        };
        fetchBookedSlots();
    }, [courtId]);

    const addEquipmentItem = () => {
        setEquipmentItems([...equipmentItems, { equipmentId: '', quantity: 1 }]);
    };

    const updateEquipmentItem = (index, field, value) => {
        const newItems = [...equipmentItems];
        newItems[index][field] = value;
        setEquipmentItems(newItems);
    };

    const removeEquipmentItem = (index) => {
        const newItems = equipmentItems.filter((_, i) => i !== index);
        setEquipmentItems(newItems);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        const start = new Date(startTime);
        const end = new Date(endTime);

        if (start >= end) {
            setError('A befejezés ideje nem lehet korábban, mint a kezdés ideje!');
            return;
        }

        const hasOverlap = bookedSlots.some(slot => {
            const slotStart = new Date(slot.startTime);
            const slotEnd = new Date(slot.endTime);
            return (start < slotEnd && end > slotStart);
        });

        if (hasOverlap) {
            setError('A megadott idősáv ütközik egy már meglévő foglalással! Kérjük, válassz másik időpontot.');
            return; 
        }
       
        const payload = {
            courtId: parseInt(courtId),
            startTime: startTime,
            endTime: endTime,
            equipmentItems: equipmentItems
                .filter(item => item.equipmentId !== '') 
                .map(item => ({
                    equipmentId: parseInt(item.equipmentId),
                    quantity: parseInt(item.quantity)
                }))
        };

        try {
            const response = await axiosInstance.post('/reservations', payload);
            alert(`Sikeres foglalás! Végösszeg: ${response.data.totalCost} Ft`);
            navigate('/my-reservations');
        } catch (err) {
            console.error("Hiba foglaláskor:", err);
            setError(err.response?.data?.message || err.response?.data?.error || 'Hiba történt a foglalás során!');
        }
    };

    const calculateEstimatedPrice = () => {
        if (!courtId || !startTime || !endTime) return 0;

        const start = new Date(startTime);
        const end = new Date(endTime);
        
        if (isNaN(start.getTime()) || isNaN(end.getTime()) || start >= end) {
            return 0;
        }

        const durationHours = (end - start) / (1000 * 60 * 60);

        const selectedCourt = courts.find(c => c.id === parseInt(courtId));
        const courtRate = selectedCourt ? selectedCourt.hourlyRate : 0;
        let total = courtRate * durationHours;

        equipmentItems.forEach(item => {
            if (item.equipmentId && item.quantity > 0) {
                const selectedEq = equipments.find(e => e.id === parseInt(item.equipmentId));
                if (selectedEq) {
                    total += (selectedEq.hourlyRate * item.quantity * durationHours);
                }
            }
        });

        return Math.round(total);
    };

    const estimatedTotal = calculateEstimatedPrice();

    return (
        <div style={{ maxWidth: '600px', margin: '40px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px', backgroundColor: '#fdfdfd' }}>
            <h2 style={{ textAlign: 'center', borderBottom: '2px solid #2ecc71', paddingBottom: '10px' }}>Új Foglalás Leadása</h2>
            
            {error && <div style={{ color: 'red', fontWeight: 'bold', marginBottom: '15px', textAlign: 'center', padding: '10px', backgroundColor: '#fdedec', borderRadius: '4px', border: '1px solid #e74c3c' }}>{error}</div>}

            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                
                {/* Pálya választó */}
                <div>
                    <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Pálya kiválasztása:</label>
                    <select 
                        value={courtId} 
                        onChange={(e) => setCourtId(e.target.value)} 
                        required
                        style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    >
                        <option value="">-- Válassz egy pályát --</option>
                        {courts.map(c => (
                            <option key={c.id} value={c.id}>{c.name} ({c.sportType}) - {c.hourlyRate} Ft/óra</option>
                        ))}
                    </select>
                </div>

                {/* ÚJ: FOGLALT IDŐPONTOK LISTÁJA (Csak akkor jelenik meg, ha van már foglalás a pályára) */}
                {courtId && bookedSlots.length > 0 && (
                    <div style={{ padding: '12px', backgroundColor: '#fff8e1', border: '1px solid #ffb300', borderRadius: '4px', fontSize: '14px' }}>
                        <strong style={{ color: '#b78103' }}>⚠️ Figyelem! A pályán a következő időpontok már FOGLALTAK:</strong>
                        <ul style={{ margin: '5px 0 0 0', paddingLeft: '20px', color: '#555', fontWeight: '500' }}>
                            {bookedSlots.map(slot => (
                                <li key={slot.id}>
                                    {new Date(slot.startTime).toLocaleString([], {year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute:'2-digit'})} 
                                    {' - '} 
                                    {new Date(slot.endTime).toLocaleString([], {year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute:'2-digit'})}
                                </li>
                            ))}
                        </ul>
                    </div>
                )}
                {courtId && bookedSlots.length === 0 && (
                    <div style={{ padding: '8px', backgroundColor: '#e8f8f5', color: '#117a65', borderRadius: '4px', fontSize: '14px', fontWeight: '500' }}>
                        ✅ Ez a pálya a választott napokon teljesen szabad, nincsenek korábbi foglalások!
                    </div>
                )}

                {/* Időpontok */}
                <div style={{ display: 'flex', gap: '10px' }}>
                    <div style={{ flex: 1 }}>
                        <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Kezdés:</label>
                        <input 
                            type="datetime-local" 
                            value={startTime} 
                            onChange={(e) => setStartTime(e.target.value)} 
                            required 
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc', boxSizing: 'border-box' }}
                        />
                    </div>
                    <div style={{ flex: 1 }}>
                        <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Befejezés:</label>
                        <input 
                            type="datetime-local" 
                            value={endTime} 
                            onChange={(e) => setEndTime(e.target.value)} 
                            required 
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc', boxSizing: 'border-box' }}
                        />
                    </div>
                </div>

                {/* Eszközök hozzáadása */}
                <div style={{ marginTop: '10px', padding: '15px', border: '1px dashed #aaa', borderRadius: '5px', backgroundColor: '#fafafa' }}>
                    <h4 style={{ margin: '0 0 10px 0' }}>Bérelt eszközök (Opcionális)</h4>
                    
                    {equipmentItems.map((item, index) => {
                        const selectedEq = equipments.find(e => e.id === parseInt(item.equipmentId));
                        
                        return (
                            <div key={index} style={{ display: 'flex', gap: '10px', marginBottom: '10px', alignItems: 'center', flexWrap: 'wrap' }}>
                                <select 
                                    value={item.equipmentId} 
                                    onChange={(e) => updateEquipmentItem(index, 'equipmentId', e.target.value)}
                                    style={{ flex: 2, padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                                >
                                    <option value="">-- Válassz eszközt --</option>
                                    {equipments.map(eq => {
                                        const isAlreadySelected = equipmentItems.some((eqItem, i) => i !== index && parseInt(eqItem.equipmentId) === eq.id);
                                        return (
                                            <option key={eq.id} value={eq.id} disabled={isAlreadySelected}>
                                                {eq.name} {isAlreadySelected ? '(Már kiválasztva)' : `(${eq.hourlyRate} Ft/óra)`}
                                            </option>
                                        );
                                    })}
                                </select>
                                
                                <div style={{ display: 'flex', alignItems: 'center', gap: '5px', flex: 1 }}>
                                    <input 
                                        type="number" 
                                        min="1" 
                                        value={item.quantity} 
                                        onChange={(e) => updateEquipmentItem(index, 'quantity', e.target.value)}
                                        style={{ width: '60px', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                                    />
                                    <span style={{ fontSize: '14px' }}>db</span>
                                </div>
                                
                                <button type="button" onClick={() => removeEquipmentItem(index)} style={{ padding: '8px 12px', backgroundColor: '#e74c3c', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>X</button>

                                {selectedEq && (
                                    <div style={{ width: '100%', fontSize: '13px', color: '#7f8c8d', marginTop: '-5px', paddingLeft: '2px' }}>
                                        Óradíj: <strong>{selectedEq.hourlyRate} Ft / óra</strong>
                                    </div>
                                )}
                            </div>
                        );
                    })}
                    
                    <button type="button" onClick={addEquipmentItem} style={{ padding: '8px 15px', backgroundColor: '#f39c12', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
                        + Eszköz hozzáadása
                    </button>
                </div>

                {estimatedTotal > 0 && (
                    <div style={{ backgroundColor: '#e8f8f5', padding: '15px', borderRadius: '5px', textAlign: 'center', fontSize: '18px', border: '1px solid #2ecc71', marginTop: '10px' }}>
                        Várható végösszeg: <strong>{estimatedTotal} Ft</strong>
                        <div style={{ fontSize: '14px', color: '#7f8c8d', marginTop: '5px' }}>
                            ({((new Date(endTime) - new Date(startTime)) / (1000 * 60 * 60)).toFixed(1)} óra foglalás)
                        </div>
                    </div>
                )}
                
                <button type="submit" style={{ padding: '12px', background: '#2ecc71', color: 'white', border: 'none', borderRadius: '4px', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer', marginTop: '10px' }}>
                    Foglalás Véglegesítése
                </button>
            </form>
        </div>
    );
};

export default CreateReservation;