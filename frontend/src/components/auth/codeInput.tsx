'use client';

import React, { useState, useRef, useEffect, useCallback } from 'react';

interface CodeInputProps {
  length: number;
  value?: string;
  onChange: (code: string) => void;
  onComplete?: (code: string) => void;
  disabled?: boolean;
}

const CodeInput: React.FC<CodeInputProps> = ({
  length,
  value = '',
  onChange,
  onComplete,
  disabled,
}) => {
  // 중복 제거: 하나의 함수로 통합
  const createCodeArray = useCallback((val: string, len: number) => {
    const arr = Array(len).fill('');
    for (let i = 0; i < Math.min(val.length, len); i++) {
      arr[i] = val[i] || '';
    }
    return arr;
  }, []);

  const [code, setCode] = useState<string[]>(() => createCodeArray(value, length));
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);
  const lastFocusSource = useRef<'auto' | 'user'>('auto'); // 포커스 소스 추적

  // value prop 변경 시에만 동기화
  useEffect(() => {
    const newCode = createCodeArray(value, length);
    setCode(newCode);
    lastFocusSource.current = 'auto'; // 자동 포커스 허용
  }, [value, length, createCodeArray]);

  // 자동 포커스는 특정 상황에서만
  useEffect(() => {
    if (lastFocusSource.current === 'auto') {
      const firstEmptyIndex = code.findIndex((digit) => digit === '');
      const focusIndex = firstEmptyIndex === -1 ? Math.max(0, code.length - 1) : firstEmptyIndex;

      // 약간의 지연을 두어 다른 상태 업데이트와 충돌 방지
      const timeoutId = setTimeout(() => {
        inputRefs.current[focusIndex]?.focus();
        lastFocusSource.current = 'user'; // 자동 포커스 완료 후 사용자 모드로 전환
      }, 0);

      return () => clearTimeout(timeoutId);
    }
  }, [code]);

  const updateCode = useCallback(
    (newCode: string[], source: 'user' | 'auto' = 'user') => {
      setCode(newCode);
      lastFocusSource.current = source;
      const codeString = newCode.join('');
      onChange(codeString);

      // 완성 시 콜백 호출
      if (newCode.every((digit) => digit !== '') && onComplete) {
        onComplete(codeString);
      }
    },
    [onChange, onComplete]
  );

  const handleChange = (inputValue: string, index: number) => {
    const sanitizedValue = inputValue.replace(/[^0-9]/g, '');
    if (sanitizedValue.length > 1) return;

    const newCode = [...code];
    newCode[index] = sanitizedValue;
    updateCode(newCode);

    // 다음 입력 필드로 이동
    if (sanitizedValue && index < length - 1) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
    const allowedKeys = [
      'Backspace',
      'Delete',
      'Tab',
      'ArrowLeft',
      'ArrowRight',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
    ];

    if (!allowedKeys.includes(e.key)) {
      e.preventDefault();
      return;
    }

    // Tab 키 처리 (일관성 있게)
    if (e.key === 'Tab') {
      if (e.shiftKey && index > 0) {
        e.preventDefault();
        inputRefs.current[index - 1]?.focus();
      } else if (!e.shiftKey && index < length - 1) {
        e.preventDefault();
        inputRefs.current[index + 1]?.focus();
      }
      return;
    }

    // Backspace 처리
    if (e.key === 'Backspace') {
      if (!code[index] && index > 0) {
        // 현재 필드가 비어있으면 이전 필드의 값을 삭제하고 이동
        const newCode = [...code];
        newCode[index - 1] = '';
        updateCode(newCode);
        inputRefs.current[index - 1]?.focus();
      } else if (code[index]) {
        // 현재 필드에 값이 있으면 삭제만
        const newCode = [...code];
        newCode[index] = '';
        updateCode(newCode);
      }
    }

    // Delete 처리
    if (e.key === 'Delete') {
      const newCode = [...code];
      newCode[index] = '';
      updateCode(newCode);
    }

    // Arrow 키 처리
    if (e.key === 'ArrowLeft' && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
    if (e.key === 'ArrowRight' && index < length - 1) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    const pastedData = e.clipboardData.getData('text');
    const sanitizedData = pastedData.replace(/[^0-9]/g, '').slice(0, length);

    if (sanitizedData) {
      const newCode = createCodeArray(sanitizedData, length);
      updateCode(newCode);

      // 붙여넣기 후 적절한 위치로 포커스
      const nextIndex = Math.min(sanitizedData.length, length - 1);
      inputRefs.current[nextIndex]?.focus();
    }
  };

  const handleFocus = (index: number) => {
    lastFocusSource.current = 'user'; // 사용자가 직접 포커스
    inputRefs.current[index]?.select();
  };

  return (
    <div className="flex justify-center space-x-2">
      {code.map((digit, index) => (
        <input
          key={index}
          ref={(el) => {
            inputRefs.current[index] = el;
          }}
          type="text"
          inputMode="numeric"
          maxLength={1}
          value={digit}
          onChange={(e) => handleChange(e.target.value, index)}
          onKeyDown={(e) => handleKeyDown(e, index)}
          onPaste={handlePaste}
          onFocus={() => handleFocus(index)}
          className={`h-12 w-12 rounded-md border text-center text-xl focus:outline-none focus:ring-2 ${
            disabled
              ? 'cursor-not-allowed border-gray-200 bg-gray-100 text-gray-400'
              : 'border-gray-300 bg-white text-gray-900 focus:border-indigo-500 focus:ring-indigo-500'
          }`}
          disabled={disabled}
          autoComplete="one-time-code"
          aria-label={`Verification code digit ${index + 1}`}
        />
      ))}
    </div>
  );
};

export default CodeInput;
