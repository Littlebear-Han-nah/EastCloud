document.addEventListener("DOMContentLoaded", () => {
    const chatContainer = document.getElementById("chatContainer");
    const chatHistory = document.getElementById("chatHistory");
    const replyBox = document.getElementById("replyBox");
    const btnMockMessage = document.getElementById("btnMockMessage");
    const orderContextBar = document.getElementById("orderContextBar");
    const currentOrderInfo = document.getElementById("currentOrderInfo");
    const aiPipeline = document.getElementById("aiPipeline");
    const queueStatusText = document.getElementById("queueStatusText");
    const systemClock = document.getElementById("systemClock");
    const replyIntent = document.getElementById("replyIntent");
    const aiDraftInput = document.getElementById("aiDraftInput");
    const btnSendReply = document.getElementById("btnSendReply");
    const pipelineSteps = Array.from(document.querySelectorAll(".step"));
    const pendingTimers = [];

    renderPlaceholder();
    syncClock();
    setInterval(syncClock, 1000);

    const stompClient = new StompJs.Client({
        brokerURL: `ws://${window.location.host}/ws`,
        webSocketFactory: () => new SockJS("/ws"),
        reconnectDelay: 5000
    });

    stompClient.onConnect = () => {
        queueStatusText.textContent = "连接已建立";
        stompClient.subscribe("/topic/messages", (message) => {
            const data = JSON.parse(message.body);
            handleIncomingMessageData(data);
        });
    };

    stompClient.onStompError = () => {
        queueStatusText.textContent = "连接异常";
    };

    stompClient.onWebSocketClose = () => {
        if (document.body.dataset.state === "idle") {
            queueStatusText.textContent = "等待重连";
        }
    };

    stompClient.activate();

    btnMockMessage.addEventListener("click", () => {
        resetInterface();
        setInterfaceState("processing");
        queueStatusText.textContent = "正在接入新工单";
        showElement(aiPipeline, "grid");

        const payload = {
            orderId: `ORD-${Math.floor(100000 + Math.random() * 900000)}`,
            text: "I bought this product last week but the packaging was damaged and the headphones have a scratch. I am very angry, this was a gift! I need a replacement ASAP or I will leave a 1-star review."
        };

        fetch("/api/mock/messages", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        }).catch((error) => {
            console.error(error);
            queueStatusText.textContent = "消息接入失败";
            renderPlaceholder("模拟消息发送失败，请检查服务是否已启动。");
            hideElement(aiPipeline);
            setInterfaceState("idle");
        });
    });

    btnSendReply.addEventListener("click", () => {
        alert("回复已通过 SP-API 安全发送，买家敏感信息已按策略脱敏并记录归档。");
        resetInterface();
    });

    function handleIncomingMessageData(data) {
        showElement(orderContextBar, "flex");
        currentOrderInfo.textContent = `订单号 ${data.orderId}`;

        if (data.status === "PENDING") {
            queueStatusText.textContent = "AI 正在分析与起草";
            setInterfaceState("processing");
            renderPendingMessage(data);
            runPipelinePreview();
            return;
        }

        if (data.status === "DRAFT_READY") {
            queueStatusText.textContent = "草稿已生成";
            activateStep(3);
            window.setTimeout(() => {
                activateStep(4);
            }, 280);

            window.setTimeout(() => {
                replyIntent.textContent = data.intentTag || "待确认";
                aiDraftInput.value = data.aiDraft || "";
                showElement(replyBox, "block");
                setInterfaceState("ready");
                replyBox.scrollIntoView({ behavior: "smooth", block: "nearest" });
            }, 760);
        }
    }

    function renderPendingMessage(data) {
        chatHistory.innerHTML = "";
        chatHistory.appendChild(createEventNote("消息已进入处理链", "系统正在同步订单上下文、客户情绪和售后知识。"));
        chatHistory.appendChild(createBuyerMessage(data.buyerMessage));
        chatHistory.scrollTop = chatHistory.scrollHeight;
    }

    function createEventNote(title, detail) {
        const note = document.createElement("div");
        note.className = "event-note";
        note.innerHTML = `<strong>${title}</strong><br>${detail}`;
        return note;
    }

    function createBuyerMessage(message) {
        const card = document.createElement("article");
        card.className = "message-card buyer";

        const header = document.createElement("div");
        header.className = "msg-header";
        header.innerHTML = `
            <span class="msg-role">Amazon Buyer</span>
            <span class="msg-badge">高情绪投诉</span>
            <span>${formatTime(new Date())}</span>
        `;

        const content = document.createElement("div");
        content.className = "msg-content";
        content.textContent = message;

        card.appendChild(header);
        card.appendChild(content);
        return card;
    }

    function runPipelinePreview() {
        clearPipeline();
        [0, 1, 2].forEach((index, sequence) => {
            const timerId = window.setTimeout(() => {
                activateStep(index + 1);
            }, 280 + sequence * 780);
            pendingTimers.push(timerId);
        });
    }

    function activateStep(stepNumber) {
        const step = pipelineSteps[stepNumber - 1];
        if (!step) {
            return;
        }

        step.classList.add("active");
    }

    function clearPipeline() {
        clearPendingTimers();
        pipelineSteps.forEach((step) => step.classList.remove("active"));
    }

    function clearPendingTimers() {
        while (pendingTimers.length) {
            window.clearTimeout(pendingTimers.pop());
        }
    }

    function resetInterface() {
        clearPipeline();
        hideElement(replyBox);
        hideElement(orderContextBar);
        hideElement(aiPipeline);
        aiDraftInput.value = "";
        replyIntent.textContent = "--";
        renderPlaceholder();
        queueStatusText.textContent = "空闲待命";
        setInterfaceState("idle");
    }

    function setInterfaceState(state) {
        document.body.dataset.state = state;
        chatContainer.dataset.state = state;
    }

    function showElement(element, displayValue) {
        element.style.display = displayValue;
        requestAnimationFrame(() => {
            element.classList.add("is-visible");
        });
    }

    function hideElement(element) {
        element.classList.remove("is-visible");
        element.style.display = "none";
    }

    function renderPlaceholder(message) {
        chatHistory.innerHTML = `
            <div class="placeholder-msg">
                <span class="placeholder-kicker">Awaiting event</span>
                <strong>工作台正处于待命状态</strong>
                <p>${message || "点击左下角演示按钮，查看消息接入、分析和回复生成的完整流程。"}</p>
            </div>
        `;
    }

    function syncClock() {
        systemClock.textContent = formatTime(new Date());
    }

    function formatTime(date) {
        return new Intl.DateTimeFormat("zh-CN", {
            hour: "2-digit",
            minute: "2-digit"
        }).format(date);
    }
});
