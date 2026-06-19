document.addEventListener('DOMContentLoaded', () => {
    // ---- File Upload Logic ----
    const fileInput = document.getElementById('fileInput');
    const fileNameDisplay = document.getElementById('fileName');
    const uploadForm = document.getElementById('uploadForm');
    const statusMessage = document.getElementById('uploadStatus');

    fileInput.addEventListener('change', (e) => {
        if (e.target.files.length > 0) {
            fileNameDisplay.textContent = e.target.files[0].name;
            fileNameDisplay.style.color = '#f8fafc';
        } else {
            fileNameDisplay.textContent = 'Selecciona un archivo para subir...';
            fileNameDisplay.style.color = '#94a3b8';
        }
    });

    uploadForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        if (fileInput.files.length === 0) {
            showMessage('Por favor selecciona un archivo primero.', 'error');
            return;
        }

        const formData = new FormData();
        formData.append('file', fileInput.files[0]);
        
        showMessage('Subiendo archivo...', ''); // Mensaje de carga

        try {
            const response = await fetch('/api/archivos/subir', {
                method: 'POST',
                body: formData
            });

            const text = await response.text();
            
            if (response.ok) {
                showMessage(text, 'success');
                uploadForm.reset();
                fileNameDisplay.textContent = 'Selecciona un archivo para subir...';
                fileNameDisplay.style.color = '#94a3b8';
            } else {
                showMessage(text, 'error');
            }
        } catch (error) {
            showMessage('Error de red al subir el archivo.', 'error');
            console.error('Upload error:', error);
        }
    });

    function showMessage(text, type) {
        statusMessage.textContent = text;
        statusMessage.className = `status-message ${type}`;
        if(type !== '') {
            setTimeout(() => {
                statusMessage.textContent = '';
                statusMessage.className = 'status-message';
            }, 6000);
        }
    }

    // ---- Scheduled Tasks Polling Logic ----
    const logsList = document.getElementById('logsList');
    
    async function fetchLogs() {
        try {
            const response = await fetch('/api/logs');
            if (response.ok) {
                const logs = await response.json();
                renderLogs(logs);
            }
        } catch (error) {
            console.error('Error fetching logs:', error);
        }
    }

    function renderLogs(logs) {
        if (logs.length === 0) return;
        
        logsList.innerHTML = ''; // Clear current logs
        logs.forEach(log => {
            const li = document.createElement('li');
            li.className = 'log-item';
            li.textContent = log;
            logsList.appendChild(li);
        });
    }

    // Consultar el backend cada 2 segundos para ver si hay nuevas ejecuciones
    setInterval(fetchLogs, 2000);
    // Primera consulta al cargar
    fetchLogs();
});
