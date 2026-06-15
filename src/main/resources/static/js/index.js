const chatBox = document.getElementById('chatBox');
const messageInput = document.getElementById('messageInput');
const apiTypeSelect = document.getElementById('apiType');
const sendBtn = document.getElementById('sendBtn');

function handleEnter(e) {
    if (e.key === 'Enter') sendMessage();
}

function appendMessage(sender, text, isHtml = false) {
    const msgDiv = document.createElement('div');
    msgDiv.className = `message ${sender}`;

    if (isHtml) {
        msgDiv.innerHTML = text;
    } else {
        msgDiv.textContent = text;
    }

    chatBox.appendChild(msgDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
    return msgDiv;
}

async function sendMessage() {
    const message = messageInput.value.trim();
    if (!message) return;

    // UI 업데이트
    appendMessage('user', message);
    messageInput.value = '';
    messageInput.disabled = true;
    sendBtn.disabled = true;

    const loadingMsg = appendMessage('bot', 'AI가 답변을 생성 중입니다... ⏳');

    const apiType = apiTypeSelect.value;
    let response;

    try {
        const [archType, modelType] = apiType.split('-');

        if (archType === 'orch' || archType === 'coordinator') {
            response = await fetch('/api/nl-search/search', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    message: message,
                    model: modelType,
                    type: archType
                })
            });
        }

        else if (archType === 'tool') {
            response = await fetch(`/api/chat/${modelType}?question=${encodeURIComponent(message)}`);
        }

        loadingMsg.remove();

        if (!response.ok) throw new Error(`서버 에러가 발생했습니다. (상태 코드: ${response.status})`);

        if (archType === 'orch' || archType === 'coordinator') {
            const data = await response.json();
            const title = archType === 'orch' ? '[Orchestrator 중앙 조율 완료]' : '[Coordinator 협업 완료]';
            let resultHtml = `<strong>${title}</strong><br><pre>${JSON.stringify(data, null, 2)}</pre>`;
            appendMessage('bot', resultHtml, true);
        } else {
            const text = await response.text();
            appendMessage('bot', text);
        }
    } catch (error) {
        loadingMsg.remove();
        appendMessage('bot', `❌ 통신 실패: ${error.message}`);
    } finally {
        messageInput.disabled = false;
        sendBtn.disabled = false;
        messageInput.focus();
    }
}