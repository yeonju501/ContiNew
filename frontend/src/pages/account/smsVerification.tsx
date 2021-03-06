import { Button } from "@components/account/Button";
import { Container } from "@components/account/Container";
import { Header } from "@components/account/Header";
import { Input } from "@components/account/Input";
import { Label } from "@components/account/Label";
import { LinkButton } from "@components/account/LinkButton";
import Timer from "@components/account/Timer";
import getErrorMessage from "@utils/getErrorMessage";
import { useRouter } from "next/router";
import { useState } from "react";
import { toast } from "react-toastify";
import authApi from "src/api/auth";

function smsVerification() {
	const [phoneNumber, setPhoneNumber] = useState({ phone_number: "" });
	const [code, setCode] = useState({ code: "" });
	const [disabled, setDisabled] = useState(true);

	const router = useRouter();

	const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		if (e.target.name === "phoneNumber") setPhoneNumber({ phone_number: e.target.value });
		else if (e.target.name === "code") setCode({ code: e.target.value });
	};

	const handleSendClick = async () => {
		await authApi.sendCode(phoneNumber).then(async (res) => {
			if (res.status) {
				setDisabled(false);
				return;
			}
			toast.error(getErrorMessage(res));
		});
	};

	const handleConfirmClick = async () => {
		await authApi.confirmCode(code).then(async (res) => {
			if (res.status) {
				alert("휴대폰 인증이 완료되었습니다");
				router.push("/account/signupComplete");
				return;
			}
			toast.error(getErrorMessage(res));
		});
	};

	return (
		<Container>
			<Header>휴대폰 인증</Header>
			<Label>휴대폰 번호</Label>
			<div>
				<Input placeholder="-없이 숫자만 입력" name="phoneNumber" onChange={handleInputChange} />
				<Button onClick={handleSendClick} backgroundColor="#dc143c" color="white">
					인증번호 전송
				</Button>
			</div>
			<Label>인증 번호</Label>
			<div>
				<Input
					placeholder="인증 번호 입력"
					disabled={disabled}
					name="code"
					onChange={handleInputChange}
				/>
				<Button
					disabled={disabled}
					onClick={handleConfirmClick}
					backgroundColor="#dc143c"
					color="white"
				>
					인증번호 확인
				</Button>
				{!disabled && <Timer />}
			</div>
			<LinkButton href="/">본인 인증 다음에 하기</LinkButton>
		</Container>
	);
}

export default smsVerification;
