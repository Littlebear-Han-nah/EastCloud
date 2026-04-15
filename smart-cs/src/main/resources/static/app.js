document.addEventListener('DOMContentLoaded', () => {
    
    const chatContainer = document.getElementById('chatContainer');
    const chatHistory = document.getElementById('chatHistory');
    const replyBox = document.getElementById('replyBox');
    const btnMockMessage = document.getElementById('btnMockMessage');
    const orderContextBar = document.getElementById('orderContextBar');
    const currentOrderInfo = document.getElementById('currentOrderInfo');
    const aiPipeline = document.getElementById('aiPipeline');
    const step1 = document.querySelector('.step-1');
    const step2 = document.querySelector('.step-2');
    const step3 = document.querySelector('.step-3');
    const step4 = document.querySelector('.step-4');

    // --- STOMP WebSocket Connection ---
    const stompClient = new StompJs.Client({
        brokerURL: `ws://${window.location.host}/ws`, 
        webSocketFactory: () => new SockJS('/ws'),
        reconnectDelay: 5000,
    });

    stompClient.onConnect = (frame) => {
        stompClient.subscribe('/topic/messages', (m) => {
            const data = JSON.parse(m.body);
            handleIncomingMessageData(data);
        });
    };
    stompClient.activate();

    // --- MOCK API CALL ---
    btnMockMessage.addEventListener('click', () => {
        // Reset UI
        replyBox.style.display = 'none';
        chatHistory.innerHTML = '';
        aiPipeline.style.display = 'flex';
        [step1, step2, step3, step4].forEach(el => el.classList.remove('active'));

        const payload = {
            orderId: 'ORD-' + Math.floor(100000 + Math.random() * 900000),
            text: 'I bought this product last week but the packaging was damaged and the headphones have a scratch. I am very angry, this was a gift! I need a replacement ASAP or I will leave a 1-star review.'
        };
        
        fetch('/api/mock/messages', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(payload)
        }).catch(err => console.error(err));
    });

    document.getElementById('btnSendReply').addEventListener('click', () => {
        alert("✔️ 成功：邮件已通过 SP-API 加密发送至亚马逊买家系统。\n\n⚠️ 合规与隐私拦截：买家敏感信息（PII）已按照 30天 保护策略完成隔离。");
        replyBox.style.display = 'none';
        orderContextBar.style.display = 'none';
        aiPipeline.style.display = 'none';
        chatHistory.innerHTML = '<div class="placeholder-msg">工作台等待接入新客诉...</div>';
    });

    // --- UI Update Helpers ---
    function handleIncomingMessageData(data) {
        orderContextBar.style.display = 'flex';
        currentOrderInfo.textContent = `订单编号: ${data.orderId}`;
        
        if(data.status === 'PENDING') {
            chatHistory.innerHTML = `
                <div class="message-card buyer">
                    <div class="msg-header">
                        <span class="msg-role">Amazon Buyer (VIP)</span>
                        <span>Just now</span>
                    </div>
                    <div class="msg-content">${data.buyerMessage}</div>
                </div>
            `;

            // Start Pipeline Animation Dummy Sequence
            setTimeout(() => step1.classList.add('active'), 200);   // Intent Analysis
            setTimeout(() => step2.classList.add('active'), 1200);  // RAG Search
            setTimeout(() => step3.classList.add('active'), 2000);  // Draft Generation
        } else if (data.status === 'DRAFT_READY') {
            setTimeout(() => {
                step4.classList.add('active'); // Compliance check
                
                setTimeout(() => {
                    document.getElementById('replyIntent').textContent = data.intentTag;
                    document.getElementById('aiDraftInput').value = data.aiDraft;
                    replyBox.style.display = 'block';
                    
                    // Smooth scroll to reply box
                    replyBox.scrollIntoView({ behavior: 'smooth', block: 'end' });
                }, 800);
            }, 800);
        }
    }
});
